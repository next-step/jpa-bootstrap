package event.impl;

import persistence.sql.context.EntityPersister;

public class ChildEntityInsertAction<T, C> extends AbstractEntityInsertAction {
    private final T parentEntity;
    private final C childEntity;
    private final EntityPersister<C> persister;

    public ChildEntityInsertAction(T parentEntity, C childEntity, EntityPersister<C> persister) {
        this.parentEntity = parentEntity;
        this.childEntity = childEntity;
        this.persister = persister;
    }

    public static <T, C> Builder<T, C> builder() {
        return new Builder<>();
    }

    @Override
    public void execute() {
        persister.insert(childEntity, parentEntity);
    }

    public boolean isDelayed() {
        return isNotIdentityGenerationType(persister.getMetadataLoader());
    }

    public static class Builder<T, C> {
        private T parentEntity;
        private C childEntity;
        private EntityPersister<C> persister;

        public Builder<T, C> parentEntity(T entity) {
            this.parentEntity = entity;
            return this;
        }

        public Builder<T, C> childEntity(C childEntity) {
            this.childEntity = childEntity;
            return this;
        }

        public Builder<T, C> persister(EntityPersister<C> persister) {
            this.persister = persister;
            return this;
        }

        public ChildEntityInsertAction<T, C> build() {
            if (parentEntity == null || childEntity == null || persister == null) {
                throw new IllegalArgumentException("Entity, child entity, and persister must be set");
            }

            return new ChildEntityInsertAction<>(parentEntity, childEntity, persister);
        }
    }
}
