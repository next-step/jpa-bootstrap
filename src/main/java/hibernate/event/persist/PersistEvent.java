package hibernate.event.persist;

import hibernate.entity.EntityPersister;
import hibernate.metamodel.MetaModel;

public class PersistEvent {

    private final EntityPersister<?> entityPersister;
    private final Object entity;

    private PersistEvent(final EntityPersister<?> entityPersister, final Object entity) {
        this.entityPersister = entityPersister;
        this.entity = entity;
    }

    public static PersistEvent createEvent(final MetaModel metaModel, final Object entity) {
        return new PersistEvent(metaModel.getEntityPersister(entity.getClass()), entity);
    }

    public EntityPersister<?> getEntityPersister() {
        return entityPersister;
    }

    public Object getEntity() {
        return entity;
    }
}
