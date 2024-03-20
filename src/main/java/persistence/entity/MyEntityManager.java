package persistence.entity;

import boot.action.ActionQueue;
import boot.metamodel.MetaModel;
import event.EventListenerGroup;
import event.EventType;
import event.delete.DeleteEvent;
import event.delete.DeleteEventListener;
import event.load.LoadEvent;
import event.load.LoadEventListener;
import event.save.SaveEvent;
import event.save.SaveEventListener;
import event.update.UpdateEvent;
import event.update.UpdateEventListener;
import persistence.persistencecontext.EntitySnapshot;
import persistence.persistencecontext.MyPersistenceContext;
import persistence.persistencecontext.PersistenceContext;

import java.util.List;

public class MyEntityManager implements EntityManager {

    private final MetaModel metaModel;
    private final EventListenerGroup eventListenerGroup;
    private final PersistenceContext persistenceContext;
    private final ActionQueue actionQueue;

    public MyEntityManager(MetaModel metaModel, EventListenerGroup eventListenerGroup, ActionQueue actionQueue) {
        this.metaModel = metaModel;
        this.eventListenerGroup = eventListenerGroup;
        this.persistenceContext = new MyPersistenceContext();
        this.actionQueue = actionQueue;
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
        DeleteEventListener listener = (DeleteEventListener) eventListenerGroup.getListener(EventType.DELETE);
        listener.onDelete(new DeleteEvent<>(entity));
        persistenceContext.removeEntity(entityMeta.extractId(entity), entity);
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
            UpdateEventListener listener = (UpdateEventListener) eventListenerGroup.getListener(EventType.UPDATE);
            listener.onUpdate(new UpdateEvent<>(entity));
            persistenceContext.addEntityEntry(entity, EntityEntryStatus.GONE);
        }
        actionQueue.executeAll();
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
