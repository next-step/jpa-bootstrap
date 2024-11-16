package event.listener;

import boot.Metamodel;
import builder.dml.EntityData;
import event.action.ActionQueue;
import event.action.EntityPersistAction;

public class PersistEventListenerImpl<T> implements EventListener<T> {

    private final ActionQueue actionQueue;
    private final Metamodel metamodel;

    public PersistEventListenerImpl(ActionQueue actionQueue, Metamodel metamodel) {
        this.actionQueue = actionQueue;
        this.metamodel = metamodel;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T handleEvent(EntityData entityData) {
        this.actionQueue.addAction(new EntityPersistAction(entityData, this.metamodel.entityPersister()));
        return (T) entityData.getEntityObjectData().getEntityInstance();
    }
}
