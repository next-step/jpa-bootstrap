package persistence.entity.event.listener;

import persistence.entity.event.EntityEvent;
import persistence.entity.persister.EntityPersister;
import persistence.sql.meta.MetaModel;

public class EntityDeleteListener extends AbstractEntityListener implements EntityListener {

    public EntityDeleteListener(MetaModel metaModel) {
        super(metaModel);
    }

    @Override
    public <T> T handleEvent(EntityEvent<T> event) {
        EntityPersister<T> entityPersister = metaModel.getEntityPersister(event.getEntityClass());
        entityPersister.delete(event.getEntity());
        return event.getEntity();
    }
}
