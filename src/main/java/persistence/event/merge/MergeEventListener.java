package persistence.event.merge;

import persistence.event.EventListener;

public interface MergeEventListener extends EventListener {
    <T> void onMerge(MergeEvent<T> mergeEvent);
}
