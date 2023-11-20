package persistence.event;

public interface MergeEventListener extends EventListener {
    void onMerge(final MergeEvent mergeEvent);
}
