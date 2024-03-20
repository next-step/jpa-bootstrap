package persistence.entity;

import boot.metamodel.MetaModel;
import event.EventListenerGroup;
import event.EventType;
import event.load.LoadEvent;
import event.load.LoadEventListener;
import event.save.SaveEvent;
import event.save.SaveEventListener;
import persistence.persistencecontext.EntitySnapshot;
import persistence.persistencecontext.MyPersistenceContext;
import persistence.persistencecontext.PersistenceContext;

import java.util.List;

public class MyEntityManager implements EntityManager {

    private final MetaModel metaModel;
    private final EventListenerGroup eventListenerGroup;
    private final PersistenceContext persistenceContext;

    public MyEntityManager(MetaModel metaModel, EventListenerGroup eventListenerGroup) {
        this.metaModel = metaModel;
        this.eventListenerGroup = eventListenerGroup;
        this.persistenceContext = new MyPersistenceContext();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T find(Class<T> clazz, Long id) {
        return (T) persistenceContext.getEntity(clazz, id)
                .orElseGet(() -> {
                    //TODO: Casting이 아니라 EventType의 eventListener 타입을 잘 조합하면 사용할 수 있을 것 같은데 도와주세요.
                    LoadEventListener listener = (LoadEventListener) eventListenerGroup.getListener(EventType.LOAD);
                    T foundEntity = listener.onLoad(new LoadEvent<>(clazz, id));
                    EntityMeta<T> entityMeta = metaModel.getEntityMetaFrom(foundEntity);
                    addToCache(entityMeta.extractId(foundEntity), foundEntity);
                    return foundEntity;
                });
    }

    @Override
    public <T> T persist(T entity) {
        persistenceContext.addEntityEntry(entity, EntityEntryStatus.SAVING);
        SaveEventListener listener = (SaveEventListener) eventListenerGroup.getListener(EventType.SAVE);
        listener.onSave(new SaveEvent<>(entity));
        EntityMeta<T> entityMeta = metaModel.getEntityMetaFrom(entity);
        addToCache(entityMeta.extractId(entity), entity);
        return entity;
    }

    @Override
    public void remove(Object entity) {
        EntityMeta<?> entityMeta = metaModel.getEntityMetaFrom(entity.getClass());
        persistenceContext.removeEntity(entityMeta.extractId(entity), entity);
        EntityPersister<?> entityPersister = metaModel.getEntityPersister(entity.getClass());
        entityPersister.delete(entity);
    }

    @Override
    public <T> T merge(T entity) {
        EntityMeta<T> entityMeta = metaModel.getEntityMetaFrom(entity);
        Object id = entityMeta.extractId(entity);
        EntitySnapshot snapshot = (EntitySnapshot) persistenceContext.getCachedDatabaseSnapshot(id, entity);
        if (snapshot.isChanged(entity)) {
            persistenceContext.addEntity(id, entity);
        }
        return entity;
    }

    @Override
    public void flush() {
        List<Object> entities = persistenceContext.getDirtyEntities();
        for (Object entity : entities) {
            EntityPersister<?> entityPersister = metaModel.getEntityPersister(entity.getClass());
            entityPersister.update(entity);
            persistenceContext.addEntityEntry(entity, EntityEntryStatus.GONE);
        }
    }

    @Override
    public <T> EntityMeta<T> getEntityMetaFrom(T entity) {
        return metaModel.getEntityMetaFrom(entity);
    }

    private void addToCache(Object id, Object entity) {
        persistenceContext.addEntity(id, entity);
        persistenceContext.getDatabaseSnapshot(id, entity);
    }
}
