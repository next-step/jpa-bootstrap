package event.listener.merge;

import boot.Metamodel;
import builder.dml.EntityData;
import event.action.ActionQueue;
import event.action.EntityMergeAction;
import event.listener.EventListener;

public class MergeEventListenerImpl implements MergeEventListener {

    private final Metamodel metamodel;
    private final ActionQueue actionQueue;

    public MergeEventListenerImpl(Metamodel metamodel, ActionQueue actionQueue) {
        this.metamodel = metamodel;
        this.actionQueue = actionQueue;
    }

    @Override
    public void onMerge(EntityData entityData) {
        this.actionQueue.addAction(new EntityMergeAction(entityData, this.metamodel.entityPersister()));
    }

}
