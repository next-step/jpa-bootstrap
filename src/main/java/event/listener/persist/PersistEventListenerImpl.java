package event.listener.persist;

import boot.Metamodel;
import builder.dml.EntityData;
import event.action.ActionQueue;
import event.action.EntityPersistAction;
import event.listener.EventListener;

public class PersistEventListenerImpl implements PersistEventListener {

    private final Metamodel metamodel;
    private final ActionQueue actionQueue;

    public PersistEventListenerImpl(Metamodel metamodel, ActionQueue actionQueue) {
        this.metamodel = metamodel;
        this.actionQueue = actionQueue;
    }

    @Override
    public void onPersist(EntityData entityData) {
        this.actionQueue.addAction(new EntityPersistAction(entityData, this.metamodel.entityPersister()));
    }

}
