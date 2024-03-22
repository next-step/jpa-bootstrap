package persistence.entity;

import bootstrap.MetaModel;
import jakarta.persistence.GenerationType;
import persistence.entity.event.EventListenerGroup;
import persistence.entity.event.EventType;
import persistence.entity.event.PersistEventListener;
import persistence.entity.event.action.ActionQueue;
import persistence.entity.event.delete.DeleteEvent;
import persistence.entity.event.load.LoadEvent;
import persistence.entity.event.load.LoadEventListener;
import persistence.entity.event.save.SaveEvent;
import persistence.entity.event.update.UpdateEvent;
import persistence.sql.column.Columns;
import persistence.sql.column.IdColumn;
import persistence.sql.dialect.Dialect;

import java.lang.reflect.Field;

public class EntityManagerImpl implements EntityManager {

    private final Dialect dialect;
    private final PersistenceContext persistContext;
    private final MetaModel metaModel;
    private final ActionQueue actionQueue;
    private final EventListenerGroup eventListenerGroup;

    public static EntityManagerImpl of(Dialect dialect, MetaModel metaModel) {
        ActionQueue actionQueue = new ActionQueue();
        return new EntityManagerImpl(dialect, metaModel, actionQueue);
    }

    public EntityManagerImpl(Dialect dialect, PersistenceContext persistContext, MetaModel metaModel, ActionQueue actionQueue) {
        this(dialect, persistContext, metaModel, actionQueue, EventListenerGroup.create(metaModel, actionQueue));
    }

    public EntityManagerImpl(Dialect dialect, MetaModel metaModel, ActionQueue actionQueue) {
        this(dialect, new HibernatePersistContext(), metaModel, actionQueue, EventListenerGroup.create(metaModel, actionQueue));
    }

    private EntityManagerImpl(Dialect dialect, PersistenceContext persistContext, MetaModel metaModel, ActionQueue actionQueue, EventListenerGroup eventListenerGroup) {
        this.dialect = dialect;
        this.persistContext = persistContext;
        this.metaModel = metaModel;
        this.actionQueue = actionQueue;
        this.eventListenerGroup = eventListenerGroup;
    }

    @Override
    public <T> T find(Class<T> clazz, Long id) {
        EntityMetaData entityMetaData = new EntityMetaData(clazz, new Columns(clazz.getDeclaredFields()));
        Object entity = persistContext.getEntity(clazz, id)
                .orElseGet(() -> {
                    LoadEventListener eventListener = eventListenerGroup.getLoadEventListener(EventType.LOAD);
                    T findEntity = eventListener.onLoad(new LoadEvent<>(id, clazz));
                    savePersistence(findEntity, id);
                    return findEntity;
                });
        persistContext.getDatabaseSnapshot(entityMetaData, id);
        return clazz.cast(entity);
    }

    @Override
    public <T> T persist(Object entity) {
        IdColumn idColumn = new IdColumn(entity);
        GenerationType generationType = idColumn.getIdGeneratedStrategy(dialect).getGenerationType();
        PersistEventListener eventListener = eventListenerGroup.getPersistEventListener(EventType.SAVE);

        if (dialect.getIdGeneratedStrategy(generationType).isAutoIncrement()) {
            Long id = idColumn.getValue();
            id = (id == null) ? 1L : id + 1;
            setIdValue(entity, getIdField(entity, idColumn), id);
            eventListener.fireEvent(new SaveEvent<>(id, entity));
            savePersistence(entity, id);
            return (T) entity;
        }

        eventListener.fireEvent(new SaveEvent<>(idColumn.getValue(), entity));
        savePersistence(entity, idColumn.getValue());

        return (T) entity;
    }

    private void setIdValue(Object entity, Field idField, long idValue) {
        try {
            idField.set(entity, idValue);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Field getIdField(Object entity, IdColumn idColumn) {
        Field idField;
        try {
            idField = entity.getClass().getDeclaredField(idColumn.getName());
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        idField.setAccessible(true);
        return idField;
    }

    @Override
    public void remove(Object entity) {
        IdColumn idColumn = new IdColumn(entity);
        PersistEventListener eventListener = eventListenerGroup.getPersistEventListener(EventType.DELETE);
        eventListener.fireEvent(new DeleteEvent<>(idColumn.getValue(), entity));
        persistContext.removeEntity(entity.getClass(), idColumn.getValue());
    }

    @Override
    public <T> T merge(T entity) {
        IdColumn idColumn = new IdColumn(entity);
        EntityMetaData entityMetaData = new EntityMetaData(entity);
        EntityMetaData previousEntity = persistContext.getSnapshot(entity, idColumn.getValue());
        if (entityMetaData.isDirty(previousEntity)) {
            PersistEventListener eventListener = eventListenerGroup.getPersistEventListener(EventType.UPDATE);
            eventListener.fireEvent(new UpdateEvent<>(idColumn.getValue(), entity));
            savePersistence(entity, idColumn.getValue());
            return entity;
        }
        return entity;
    }

    private void savePersistence(Object entity, Object id) {
        persistContext.getDatabaseSnapshot(new EntityMetaData(entity), id);
        persistContext.addEntity(entity, id);
    }


    @Override
    public void flush() {
        actionQueue.executeAllActions();
    }

    @Override
    public void clear() {
        persistContext.clear();
    }

    @Override
    public Dialect getDialect() {
        return dialect;
    }

}
