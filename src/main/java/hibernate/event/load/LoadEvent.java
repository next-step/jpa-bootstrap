package hibernate.event.load;

import hibernate.entity.EntityLoader;
import hibernate.metamodel.MetaModel;

public class LoadEvent<T> {

    private final EntityLoader<T> entityLoader;
    private final Object entityId;

    private LoadEvent(final EntityLoader<T> entityLoader, final Object entityId) {
        this.entityLoader = entityLoader;
        this.entityId = entityId;
    }

    public static <T> LoadEvent<T> createEvent(final MetaModel metaModel, final Class<T> clazz, final Object entityId) {
        return new LoadEvent<>(metaModel.getEntityLoader(clazz), entityId);
    }

    public EntityLoader<T> getEntityLoader() {
        return entityLoader;
    }

    public Object getEntityId() {
        return entityId;
    }
}
