package persistence.event.merge;

public interface MergeEventListener {
    <T> void onMerge(MergeEvent<T> mergeEvent);
}