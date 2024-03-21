package persistence.entity.event.listener;

import persistence.entity.event.EntityEvent;
import persistence.entity.event.action.ActionQueue;
import persistence.entity.event.action.UpdateEntityAction;
import persistence.sql.meta.MetaModel;

public class EntityMergeListener extends AbstractEntityListener implements EntityListener {

    public EntityMergeListener(MetaModel metaModel) {
        super(metaModel);
    }

    @Override
    public <T> T handleEvent(EntityEvent<T> event, ActionQueue actionQueue) {
        actionQueue.addAction(new UpdateEntityAction<>(metaModel.getEntityPersister(event.getEntityClass()),
            event.getEntity()), event.getActionType());
        return event.getEntity();
    }
}
