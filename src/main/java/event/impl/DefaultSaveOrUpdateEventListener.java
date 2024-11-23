package event.impl;

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

public class DefaultSaveOrUpdateEventListener<T> extends SaveOrUpdateEventListener<T> {

    @Override
    public void onSaveOrUpdate(SaveOrUpdateEvent<T> event) {
        if (isNew(event.entity(), event.entityManager())) {
            onSave(event);
            return;
        }

        onUpdate(event);
    }

    private void onSave(SaveOrUpdateEvent<T> event) {
        EntityManager entityManager = event.entityManager();
        PersistenceContext persistenceContext = entityManager.getPersistenceContext();
        EntityPersister<T> entityPersister = entityManager.getEntityPersister(event.entityType());

        T entity = event.entity();
        Status status = Status.MANAGED;
        EntityInsertAction<T> action = new EntityInsertAction<>(entity, entityPersister);

        if (action.isDelayed()) {
            status = Status.SAVING;
        }

        entityManager.getActionQueue().addInsertion(action);
        persistenceContext.addEntry(entity, status, entityPersister);
        if (existsPersistChildEntity(entity, event.metadataLoader())) {
            persistChildEntity(entity, entityManager);
        }

    }

    private void onUpdate(SaveOrUpdateEvent<T> event) {
        EntityManager entityManager = event.entityManager();
        PersistenceContext persistenceContext = entityManager.getPersistenceContext();
        MetadataLoader<T> loader = entityManager.getMetadataLoader(event.entityType());
        EntityPersister<T> entityPersister = entityManager.getEntityPersister(event.entityType());
        Transaction transaction = entityManager.getTransaction();
        T entity = event.entity();

        Object id = Clause.extractValue(loader.getPrimaryKeyField(), entity);

        EntityEntry entry = persistenceContext.getEntry(entity.getClass(), id);
        if (entry == null) {
            throw new IllegalStateException("Entity not found. ");
        }

        entry.updateEntity(entity);
        entityManager.getActionQueue().addUpdate(new EntityUpdateAction<>(entity, (T) entry.getSnapshot(), entityPersister));
        if (!transaction.isActive()) {
            entry.synchronizingSnapshot();
        }
    }

    private void persistChildEntity(T entity, EntityManager entityManager) {
        MetadataLoader<?> loader = entityManager.getMetadataLoader(entity.getClass());
        List<Field> fields = loader.getFieldAllByPredicate(this::isCascadePersist);

        for (Field field : fields) {
            persistChildEntities(entity, field, entityManager);
        }
    }

    private void persistChildEntities(T entity, Field field, EntityManager entityManager) {
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

    private <C> void persistIfIsNewChildEntity(T entity, C childEntity, EntityManager entityManager) {
        if (isNew(childEntity, entityManager)) {
            PersistenceContext persistenceContext = entityManager.getPersistenceContext();
            Class<C> childEntityType = (Class<C>) childEntity.getClass();
            EntityPersister<C> entityPersister = entityManager.getEntityPersister(childEntityType);
            Status status = Status.MANAGED;
            ChildEntityInsertAction<T, C> action = new ChildEntityInsertAction<>(entity, childEntity, entityPersister);

            if (action.isDelayed()) {
                status = Status.SAVING;
            }

            entityManager.getActionQueue().addChildEntityInsertion(action);
            persistenceContext.addEntry(childEntity, status, entityPersister);
        }
    }

    private boolean isNew(Object entity, EntityManager entityManager) {
        EntityPersister<?> entityPersister = entityManager.getEntityPersister(entity.getClass());

        Object id = entityPersister.getIdentifier(entity);
        if (isUnsaved(id, entityPersister)) {
            return true;
        }

        return entityManager.find(entity.getClass(), id) == null;
    }

    private boolean isUnsaved(Object id, EntityPersister<?> entityPersister) {
        return entityPersister.isIdentifierUnsaved(id);
    }

    private boolean existsPersistChildEntity(T entity, MetadataLoader<?> loader) {
        List<Field> fields = loader.getFieldAllByPredicate(this::isCascadePersist);

        return fields.stream().anyMatch(field -> isNotEmptyField(entity, field));
    }

    private boolean isCascadePersist(Field field) {
        OneToMany anno = field.getAnnotation(OneToMany.class);

        return anno != null
                && Arrays.stream(anno.cascade())
                .anyMatch(cascadeType -> CascadeType.PERSIST == cascadeType);
    }

    private boolean isNotEmptyField(T entity, Field field) {
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
