package hibernate.event.merge;

import hibernate.event.EventListener;

public interface MergeEventListener extends EventListener {

    <T> void onMerge(MergeEvent<T> event);
}
