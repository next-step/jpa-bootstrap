package hibernate.event.merge;

public class SimpleMergeEventListener implements MergeEventListener {

    @Override
    public <T> void onMerge(MergeEvent<T> event) {
        event.getEntityPersister()
                .update(event.getEntityId(), event.getChangeColumns());
    }
}
