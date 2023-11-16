package hibernate.event.load;

import hibernate.entity.EntityLoader;
import hibernate.metamodel.MetaModel;

public class LoadEvent {

    private final EntityLoader<?> entityLoader;
    private final Object entityId;

    private LoadEvent(final EntityLoader<?> entityLoader, final Object entityId) {
        this.entityLoader = entityLoader;
        this.entityId = entityId;
    }

    public static LoadEvent createEvent(final MetaModel metaModel, final Class<?> clazz, final Object entityId) {
        return new LoadEvent(metaModel.getEntityLoader(clazz), entityId);
    }

    public EntityLoader<?> getEntityLoader() {
        return entityLoader;
    }

    public Object getEntityId() {
        return entityId;
    }
}
