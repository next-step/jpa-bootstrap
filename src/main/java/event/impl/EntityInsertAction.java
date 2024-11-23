package event.impl;

import persistence.sql.context.EntityPersister;

import java.util.Objects;

public class EntityInsertAction<T> extends AbstractEntityInsertAction {
    private final T entity;
    private final EntityPersister<T> persister;

    public EntityInsertAction(T entity, EntityPersister<T> persister) {
        this.entity = entity;
        this.persister = persister;
    }

    @SuppressWarnings("unchecked")
    public static <T> EntityInsertAction<T> create(EntityPersister<?> persister, Object entity, Class<T> entityType) {
        if (entity.getClass() != entityType) {
            throw new IllegalArgumentException("Entity type mismatch");
        }

        return new EntityInsertAction<>((T) entity, (EntityPersister<T>) persister);
    }

    @Override
    public void execute() {
        persister.insert(entity);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EntityInsertAction<?> that)) {
            return false;
        }
        return Objects.equals(entity, that.entity) && Objects.equals(persister, that.persister);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entity, persister);
    }

    public boolean isDelayed() {
        return isNotIdentityGenerationType(persister.getMetadataLoader());
    }
}
