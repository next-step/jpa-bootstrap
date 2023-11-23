package persistence.context;

import jakarta.persistence.GenerationType;
import persistence.actionqueue.ActionQueue;
import persistence.actionqueue.EntityInsertAction;
import persistence.actionqueue.EntityUpdateAction;
import persistence.entity.attribute.EntityAttribute;
import persistence.entity.attribute.EntityAttributes;
import persistence.entity.attribute.id.IdAttribute;
import persistence.entity.entry.EntityEntries;
import persistence.entity.persister.EntityPersister;

import java.lang.reflect.Field;
import java.util.Optional;

import static persistence.entity.entry.Status.*;

public class PersistenceContextImpl implements PersistenceContext {
    private final EntityAttributes entityAttributes;
    private final EntityPersister simpleEntityPersister;
    private final EntityEntries entityEntries = new EntityEntries();
    private final FirstCaches firstCaches = new FirstCaches();
    private final SnapShots snapShots = new SnapShots();
    private final ActionQueue actionQueue = new ActionQueue();

    public PersistenceContextImpl(EntityPersister simpleEntityPersister, EntityAttributes entityAttributes) {
        this.simpleEntityPersister = simpleEntityPersister;
        this.entityAttributes = entityAttributes;
    }

    @Override
    public <T> T getEntity(Class<T> clazz, String id) {
        Object firstCache = firstCaches.getFirstCacheOrNull(clazz, id);
        EntityAttribute entityAttribute = entityAttributes.findEntityAttribute(clazz);

        if (firstCache == null) {
            T loaded = simpleEntityPersister.load(clazz, id);

            if (loaded == null) {
                return null;
            }

            entityEntries.changeOrSetStatus(LOADING, loaded);
            firstCaches.putFirstCache(loaded, id, entityAttribute);
            snapShots.putSnapShot(loaded, id);

            return loaded;
        }

        return clazz.cast(firstCache);
    }

    @Override
    public <T> T addEntity(T instance) {
        EntityAttribute entityAttribute = entityAttributes.findEntityAttribute(instance.getClass());
        IdAttribute idAttribute = entityAttribute.getIdAttribute();

        Field idField = idAttribute.getField();

        String instanceId = getInstanceIdAsString(instance, idField);

        if (isNewInstance(instanceId)) {
            if (idAttribute.getGenerationType() == GenerationType.IDENTITY) {
                return insert(instance, idAttribute);
            }
            actionQueue.addAction(new EntityInsertAction(instance, simpleEntityPersister, null));

            return instance;
        }

        T snapshot = getDatabaseSnapshot(instance, instanceId);

        snapShots.putSnapShot(instance, instanceId);
        firstCaches.putFirstCache(instance, instanceId, entityAttribute);
        entityEntries.changeOrSetStatus(MANAGED, instance);

        actionQueue.addAction(new EntityUpdateAction(instance, simpleEntityPersister, snapshot));

        return instance;
    }

    @Override
    public <T> void removeEntity(T instance) {
        EntityAttribute entityAttribute = entityAttributes.findEntityAttribute(instance.getClass());
        Field idField = entityAttribute.getIdAttribute().getField();
        Class<?> clazz = instance.getClass();
        String instanceId = getInstanceIdAsString(instance, idField);

        firstCaches.remove(clazz, instanceId);
        snapShots.remove(clazz, instanceId);
        entityEntries.changeOrSetStatus(DELETED, instance);

        simpleEntityPersister.remove(instance, instanceId);
        entityEntries.changeOrSetStatus(GONE, instance);
    }

    @Override
    public <T> T getDatabaseSnapshot(T instance, String instanceId) {
        Object snapshot = snapShots.getSnapShotOrNull(instance.getClass(), instanceId);

        if (snapshot == null) {
            entityEntries.changeOrSetStatus(LOADING, instance);
            Object loaded = simpleEntityPersister.load(instance.getClass(), instanceId);

            T newSnapShot = (T) snapShots.putSnapShot(loaded, instanceId);
            entityEntries.changeOrSetStatus(MANAGED, instance);
            return newSnapShot;
        }
        return (T) snapshot;
    }

    @Override
    public void flush() {
        actionQueue.executeActions();
    }

    private boolean isNewInstance(String instanceId) {
        return instanceId == null;
    }

    private <T> T insert(T instance, IdAttribute idAttribute) {
        assert idAttribute.getGenerationType() != null;

        T inserted = simpleEntityPersister.insert(instance);
        String insertedId = getInstanceIdAsString(instance, idAttribute.getField());

        firstCaches.putFirstCache(inserted, insertedId);
        snapShots.putSnapShot(inserted, insertedId);
        entityEntries.changeOrSetStatus(MANAGED, instance);

        return inserted;
    }

    private <T> String getInstanceIdAsString(T instance, Field idField) {
        idField.setAccessible(true);

        try {
            return Optional.ofNullable(idField.get(instance)).map(String::valueOf).orElse(null);

        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
