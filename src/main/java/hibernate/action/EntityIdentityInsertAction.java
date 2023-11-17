package hibernate.action;

import hibernate.entity.EntityPersister;
import hibernate.entity.meta.column.EntityColumn;

public class EntityIdentityInsertAction<T> extends EntityInsertAction<T> {

    private final EntityColumn entityId;

    public EntityIdentityInsertAction(final EntityPersister<T> entityPersister, final T entity, final EntityColumn entityId) {
        super(entityPersister, entity);
        this.entityId = entityId;
    }

    @Override
    public void execute() {
        Object id = entityPersister.insert(entity);
        entityId.assignFieldValue(entity, id);
    }
}
