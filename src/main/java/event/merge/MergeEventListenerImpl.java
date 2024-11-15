package event.merge;

import boot.Metamodel;
import builder.dml.EntityData;
import event.action.ActionQueue;
import event.action.EntityMergeAction;

public class MergeEventListenerImpl implements MergeEventListener {

    private final ActionQueue actionQueue;
    private final Metamodel metamodel;

    public MergeEventListenerImpl(ActionQueue actionQueue, Metamodel metamodel) {
        this.actionQueue = actionQueue;
        this.metamodel = metamodel;
    }

    @Override
    public void onMerge(EntityData entityData) {
        this.actionQueue.addAction(new EntityMergeAction(entityData, this.metamodel.entityPersister()));
    }
}
