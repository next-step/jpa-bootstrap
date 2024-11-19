package event.listener.delete;

import boot.Metamodel;
import builder.dml.EntityData;
import event.action.ActionQueue;
import event.action.EntityDeleteAction;
import event.listener.EventListener;

public class DeleteEventListenerImpl implements DeleteEventListener {

    private final Metamodel metamodel;
    private final ActionQueue actionQueue;

    public DeleteEventListenerImpl(Metamodel metamodel, ActionQueue actionQueue) {
        this.metamodel = metamodel;
        this.actionQueue = actionQueue;
    }

    @Override
    public void onDelete(EntityData entityData) {
        this.actionQueue.addAction(new EntityDeleteAction(entityData, this.metamodel.entityPersister()));
    }

}
