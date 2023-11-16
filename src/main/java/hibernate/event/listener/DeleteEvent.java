package hibernate.event.listener;

import hibernate.entity.EntityPersister;
import hibernate.metamodel.MetaModel;

public class DeleteEvent {

    private final EntityPersister<?> entityPersister;
    private final Object entity;

    private DeleteEvent(final EntityPersister<?> entityPersister, final Object entity) {
        this.entityPersister = entityPersister;
        this.entity = entity;
    }

    public static DeleteEvent createEvent(final MetaModel metaModel, final Object entity) {
        return new DeleteEvent(metaModel.getEntityPersister(entity.getClass()), entity);
    }

    public EntityPersister<?> getEntityPersister() {
        return entityPersister;
    }

    public Object getEntity() {
        return entity;
    }
}
