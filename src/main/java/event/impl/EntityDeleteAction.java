package event.impl;

import event.EntityAction;
import persistence.sql.context.EntityPersister;

public class EntityDeleteAction<T> implements EntityAction {
    private final T entity;
    private final EntityPersister<T> persister;

    public EntityDeleteAction(T entity, EntityPersister<T> persister) {
        this.entity = entity;
        this.persister = persister;
    }

    @SuppressWarnings("unchecked")
    public static <T> EntityDeleteAction<T> create(EntityPersister<?> persister, Object entity, Class<T> entityType) {
        if (entity.getClass() != entityType) {
            throw new IllegalArgumentException("Entity type mismatch");
        }

        return new EntityDeleteAction<>((T) entity, (EntityPersister<T>) persister);
    }

    @Override
    public void execute() {
        persister.delete(entity);
    }
}
