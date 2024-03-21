package persistence.entity.event.listener;

import persistence.entity.event.EntityEvent;
import persistence.entity.loader.EntityLoader;
import persistence.sql.meta.MetaModel;

public class EntityLoadListener extends AbstractEntityListener implements EntityListener {

    public EntityLoadListener(MetaModel metaModel) {
        super(metaModel);
    }

    @Override
    public <T> T handleEvent(EntityEvent<T> event) {
        EntityLoader<T> entityLoader = metaModel.getEntityLoader(event.getEntityClass());
        return entityLoader.find(event.getEntityId());
    }
}
