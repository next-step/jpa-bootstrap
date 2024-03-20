package boot.action;

import persistence.entity.EntityMeta;
import persistence.entity.EntityPersister;

public class EntityInsertAction<T> implements EntityAction {

    private final T entity;
    private final EntityPersister<T> entityPersister;
    private final EntityMeta<T> entityMeta;

    public EntityInsertAction(T entity, EntityPersister<T> entityPersister, EntityMeta<T> entityMeta) {
        this.entity = entity;
        this.entityPersister = entityPersister;
        this.entityMeta = entityMeta;
    }

    @Override
    public void execute() {
        Object generatedId = entityPersister.insert(entity);
        entityMeta.injectId(entity, generatedId);
    }
}
