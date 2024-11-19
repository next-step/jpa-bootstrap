package event.impl;

import event.Event;
import event.SaveOrUpdateEventListener;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import persistence.sql.clause.Clause;
import persistence.sql.context.EntityPersister;
import persistence.sql.context.PersistenceContext;
import persistence.sql.dml.EntityManager;
import persistence.sql.dml.MetadataLoader;
import persistence.sql.entity.EntityEntry;
import persistence.sql.entity.data.Status;
import persistence.sql.transaction.Transaction;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class DefaultSaveOrUpdateEventListener implements SaveOrUpdateEventListener {

    @Override
    public void onEvent(Event event) {
        if (event instanceof SaveOrUpdateEvent saveOrUpdateEvent) {
            onSaveOrUpdate(saveOrUpdateEvent);
            return;
        }

        throw new IllegalArgumentException("Event type not supported: " + event.getClass().getName());
    }

    @Override
    public void onSaveOrUpdate(SaveOrUpdateEvent event) {
        EntityManager entityManager = event.entityManager();

        if (entityManager.isNew(event.entity())) {
            onSave(event);
            return;
        }

        onUpdate(event);
    }

    private void onSave(SaveOrUpdateEvent event) {
        EntityManager entityManager = event.entityManager();
        PersistenceContext persistenceContext = entityManager.getPersistenceContext();
        EntityPersister<?> entityPersister = entityManager.getEntityPersister(event.entityType());

        Object entity = event.entity();
        entityPersister.insert(entity);
        persistenceContext.addEntry(entity, Status.SAVING, entityPersister);

        if (existsPersistChildEntity(entity, event.metadataLoader())) {
            persistChildEntity(entity, entityManager);
        }

    }

    private void onUpdate(SaveOrUpdateEvent event) {
        EntityManager entityManager = event.entityManager();
        PersistenceContext persistenceContext = entityManager.getPersistenceContext();
        MetadataLoader<?> loader = entityManager.getMetadataLoader(event.entityType());
        Transaction transaction = entityManager.getTransaction();
        Object entity = event.entity();

        Object id = Clause.extractValue(loader.getPrimaryKeyField(), entity);

        EntityEntry entry = persistenceContext.getEntry(entity.getClass(), id);
        if (entry == null) {
            throw new IllegalStateException("Entity not found. ");
        }

        entry.updateEntity(entity);
        if (!transaction.isActive()) {
            EntityPersister<?> entityPersister = entityManager.getEntityPersister(event.entityType());
            entityPersister.update(entity, entry.getSnapshot());
            entry.synchronizingSnapshot();
            persistenceContext.cleanup();
        }
    }

    private <T> void persistChildEntity(T entity, EntityManager entityManager) {
        MetadataLoader<?> loader = entityManager.getMetadataLoader(entity.getClass());
        List<Field> fields = loader.getFieldAllByPredicate(this::isCascadePersist);

        for (Field field : fields) {
            persistChildEntities(entity, field, entityManager);
        }
    }

    private <T> void persistChildEntities(T entity, Field field, EntityManager entityManager) {
        try {
            field.setAccessible(true);
            Collection<?> childEntities = (Collection<?>) field.get(entity);

            if (childEntities == null || childEntities.isEmpty()) {
                return;
            }

            childEntities.forEach(childEntity -> persistIfIsNewChildEntity(entity, childEntity, entityManager));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private <T, P> void persistIfIsNewChildEntity(T entity, P childEntity, EntityManager entityManager) {
        if (entityManager.isNew(childEntity)) {
            Transaction transaction = entityManager.getTransaction();
            PersistenceContext persistenceContext = entityManager.getPersistenceContext();
            EntityPersister<P> entityPersister = entityManager.getEntityPersister((Class<P>) childEntity.getClass());
            entityPersister.insert(childEntity, entity);
            persistenceContext.addEntry(childEntity, Status.SAVING, entityPersister);

            if (!transaction.isActive()) {
                persistenceContext.cleanup();
            }
        }
    }

    private <T> boolean existsPersistChildEntity(T entity, MetadataLoader<?> loader) {
        List<Field> fields = loader.getFieldAllByPredicate(this::isCascadePersist);

        return fields.stream().anyMatch(field -> isNotEmptyField(entity, field));
    }

    private boolean isCascadePersist(Field field) {
        OneToMany anno = field.getAnnotation(OneToMany.class);

        return anno != null
                && Arrays.stream(anno.cascade())
                .anyMatch(cascadeType -> CascadeType.PERSIST == cascadeType);
    }

    private <T> boolean isNotEmptyField(T entity, Field field) {
        try {
            field.setAccessible(true);
            Object value = field.get(entity);

            return Collection.class.isAssignableFrom(field.getType()) && !((Collection<?>) value).isEmpty();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
