package hibernate.entity;

import hibernate.action.ActionQueue;
import hibernate.entity.meta.column.EntityColumn;
import hibernate.entity.persistencecontext.EntityKey;
import hibernate.entity.persistencecontext.EntitySnapshot;
import hibernate.entity.persistencecontext.PersistenceContext;
import hibernate.event.EventListener;
import hibernate.event.EventListenerRegistry;
import hibernate.event.EventType;
import hibernate.event.delete.DeleteEvent;
import hibernate.event.delete.DeleteEventListener;
import hibernate.event.load.LoadEvent;
import hibernate.event.load.LoadEventListener;
import hibernate.event.merge.MergeEvent;
import hibernate.event.merge.MergeEventListener;
import hibernate.event.persist.PersistEvent;
import hibernate.event.persist.PersistEventListener;
import hibernate.metamodel.MetaModel;

import java.util.Map;

import static hibernate.entity.entityentry.Status.*;

public class EntityManagerImpl implements EntityManager {

    private final PersistenceContext persistenceContext;
    private final MetaModel metaModel;
    private final EventListenerRegistry eventListenerRegistry;
    private final ActionQueue actionQueue;

    public EntityManagerImpl(
            final PersistenceContext persistenceContext,
            final MetaModel metaModel,
            final EventListenerRegistry eventListenerRegistry,
            final ActionQueue actionQueue
    ) {
        this.persistenceContext = persistenceContext;
        this.metaModel = metaModel;
        this.eventListenerRegistry = eventListenerRegistry;
        this.actionQueue = actionQueue;
    }

    @Override
    public <T> T find(final Class<T> clazz, final Object id) {
        EntityKey entityKey = new EntityKey(id, clazz);
        Object persistenceContextEntity = persistenceContext.getEntity(entityKey);
        if (persistenceContextEntity != null) {
            return (T) persistenceContextEntity;
        }

        EventListener<LoadEventListener> listener = eventListenerRegistry.getListener(EventType.LOAD);
        T loadEntity = listener.fireWithReturn(new LoadEvent<>(clazz, id), LoadEventListener::onLoad);

        persistenceContext.addEntity(id, loadEntity, LOADING);
        return loadEntity;
    }

    @Override
    public void persist(final Object entity) {
        PersistEvent<?> persistEvent = PersistEvent.createEvent(metaModel, entity);
        EventListener<PersistEventListener> listener = eventListenerRegistry.getListener(EventType.PERSIST);

        EntityColumn entityId = metaModel.getEntityId(entity.getClass());
        Object id = entityId.getFieldValue(entity);
        if (id == null) {
            persistenceContext.addEntityEntry(entity, SAVING);
            listener.fireJustRun(persistEvent, PersistEventListener::onPersist);
            persistenceContext.addEntity(entityId.getFieldValue(entity), entity);
            return;
        }

        if (persistenceContext.getEntity(new EntityKey(id, entity)) != null) {
            throw new IllegalStateException("이미 영속화되어있는 entity입니다.");
        }
        persistenceContext.addEntity(id, entity, SAVING);
        listener.fireJustRun(persistEvent, PersistEventListener::onPersist);
    }

    @Override
    public void merge(final Object entity) {
        Object entityId = getNotNullEntityId(entity);
        Map<EntityColumn, Object> changedColumns = getSnapshot(entity, entityId).changedColumns(entity);
        if (changedColumns.isEmpty()) {
            return;
        }
        persistenceContext.addEntity(entityId, entity);
        EventListener<MergeEventListener> listener = eventListenerRegistry.getListener(EventType.MERGE);
        listener.fireJustRun(new MergeEvent<>(entity.getClass(), entityId, changedColumns), MergeEventListener::onMerge);
    }

    private Object getNotNullEntityId(final Object entity) {
        Object entityId = metaModel.getEntityId(entity.getClass())
                .getFieldValue(entity);
        if (entityId == null) {
            throw new IllegalStateException("id가 없는 entity는 merge할 수 없습니다.");
        }
        return entityId;
    }

    private EntitySnapshot getSnapshot(final Object entity, final Object entityId) {
        EntityKey entityKey = new EntityKey(entityId, entity.getClass());
        EntitySnapshot snapshot = persistenceContext.getDatabaseSnapshot(entityKey);
        if (snapshot == null) {
            find(entity.getClass(), entityId);
            return persistenceContext.getDatabaseSnapshot(entityKey);
        }
        return snapshot;
    }

    @Override
    public void remove(final Object entity) {
        persistenceContext.addEntityEntry(entity, DELETED);
        EventListener<DeleteEventListener> listener = eventListenerRegistry.getListener(EventType.DELETE);
        listener.fireJustRun(DeleteEvent.createEvent(entity), DeleteEventListener::onDelete);
        persistenceContext.removeEntity(entity);
    }

    @Override
    public void flush() {
        actionQueue.executeAllActions();
    }
}
