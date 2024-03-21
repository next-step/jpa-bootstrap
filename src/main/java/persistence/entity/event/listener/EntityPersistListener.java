package persistence.entity.event.listener;

import persistence.entity.event.EntityEvent;
import persistence.entity.event.action.ActionQueue;
import persistence.entity.event.action.InsertEntityAction;
import persistence.sql.meta.MetaModel;

public class EntityPersistListener extends AbstractEntityListener implements EntityListener {

    public EntityPersistListener(MetaModel metaModel) {
        super(metaModel);
    }

    @Override
    public <T> T handleEvent(EntityEvent<T> event, ActionQueue actionQueue) {
        actionQueue.addAction(new InsertEntityAction<>(metaModel.getEntityPersister(event.getEntityClass()),
            event.getEntity()), event.getActionType());
        return event.getEntity();
    }
}
