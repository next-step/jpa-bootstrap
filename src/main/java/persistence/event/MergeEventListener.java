package persistence.event;

public interface MergeEventListener {
    void onMerge(final MergeEvent mergeEvent);
}
