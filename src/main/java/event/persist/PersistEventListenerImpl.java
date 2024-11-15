package event.persist;

import boot.Metamodel;
import builder.dml.EntityData;
import event.action.ActionQueue;
import event.action.EntityPersistAction;

public class PersistEventListenerImpl implements PersistEventListener{

    private final ActionQueue actionQueue;
    private final Metamodel metamodel;

    public PersistEventListenerImpl(ActionQueue actionQueue, Metamodel metamodel) {
        this.actionQueue = actionQueue;
        this.metamodel = metamodel;
    }

    @Override
    public void onPersist(EntityData entityData) {
        this.actionQueue.addAction(new EntityPersistAction(entityData, this.metamodel.entityPersister()));
    }
}
