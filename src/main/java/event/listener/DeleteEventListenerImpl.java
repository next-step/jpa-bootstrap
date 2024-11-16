package event.listener;

import boot.Metamodel;
import builder.dml.EntityData;
import event.action.ActionQueue;
import event.action.EntityDeleteAction;

public class DeleteEventListenerImpl<T> implements EventListener<T> {

    private final ActionQueue actionQueue;
    private final Metamodel metamodel;

    public DeleteEventListenerImpl(ActionQueue actionQueue, Metamodel metamodel) {
        this.actionQueue = actionQueue;
        this.metamodel = metamodel;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T handleEvent(EntityData entityData) {
        this.actionQueue.addAction(new EntityDeleteAction(entityData, this.metamodel.entityPersister()));
        return (T) entityData.getEntityObjectData().getEntityInstance();
    }

}
