package hibernate.action;

import hibernate.entity.EntityPersister;
import hibernate.entity.meta.column.EntityColumn;

import java.util.Map;

public class EntityUpdateAction<T> extends AbstractEntityAction<T> {

    private final Object entityId;
    private final Map<EntityColumn, Object> changeColumns;

    public EntityUpdateAction(final EntityPersister<T> entityPersister, final Object entityId, final Map<EntityColumn, Object> changeColumns) {
        super(entityPersister);
        this.entityId = entityId;
        this.changeColumns = changeColumns;
    }

    @Override
    public void execute() {
        entityPersister.update(entityId, changeColumns);
    }
}
