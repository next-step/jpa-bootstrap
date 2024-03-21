package persistence.entity.event.listener;

import persistence.entity.event.EntityEvent;
import persistence.entity.persister.EntityPersister;
import persistence.sql.meta.MetaModel;

public class EntityMergeListener extends AbstractEntityListener implements EntityListener {

    public EntityMergeListener(MetaModel metaModel) {
        super(metaModel);
    }

    @Override
    public <T> T handleEvent(EntityEvent<T> event) {
        EntityPersister<T> entityPersister = metaModel.getEntityPersister(event.getEntityClass());
        entityPersister.update(event.getEntity());
        return event.getEntity();
    }
}
