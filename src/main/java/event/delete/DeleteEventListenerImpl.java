package event.delete;

import boot.Metamodel;
import builder.dml.EntityData;
import event.action.ActionQueue;
import event.action.EntityDeleteAction;

public class DeleteEventListenerImpl implements DeleteEventListener {

    private final ActionQueue actionQueue;
    private final Metamodel metamodel;

    public DeleteEventListenerImpl(ActionQueue actionQueue, Metamodel metamodel) {
        this.actionQueue = actionQueue;
        this.metamodel = metamodel;
    }

    @Override
    public void onDelete(EntityData entityData) {
        this.actionQueue.addAction(new EntityDeleteAction(entityData, this.metamodel.entityPersister()));
    }
}
