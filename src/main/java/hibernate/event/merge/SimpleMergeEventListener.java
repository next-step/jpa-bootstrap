package hibernate.event.merge;

import hibernate.action.EntityUpdateAction;

public class SimpleMergeEventListener implements MergeEventListener {

    @Override
    public <T> void onMerge(final MergeEvent<T> event) {
        event.getActionQueue()
                .addAction(new EntityUpdateAction<>(event.getEntityPersister(), event.getEntityId(), event.getChangeColumns()));
    }
}
