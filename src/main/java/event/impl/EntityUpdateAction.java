package event.impl;

import event.EntityAction;
import persistence.sql.context.EntityPersister;

public class EntityUpdateAction<T> implements EntityAction {
    private final T newEntity;
    private final T oldEntity;
    private final EntityPersister<T> persister;

    public EntityUpdateAction(T newEntity, T oldEntity, EntityPersister<T> persister) {
        this.newEntity = newEntity;
        this.oldEntity = oldEntity;
        this.persister = persister;
    }

    @SuppressWarnings("unchecked")
    public static <T> EntityUpdateAction<T> create(EntityPersister<?> persister, Object entity, Object snapshot, Class<T> entityType) {
        if (entity.getClass() != entityType) {
            throw new IllegalArgumentException("Entity type mismatch");
        }

        return new EntityUpdateAction<>((T) entity, (T) snapshot, (EntityPersister<T>) persister);

    }

    @Override
    public void execute() {
        persister.update(newEntity, oldEntity);
    }
}
