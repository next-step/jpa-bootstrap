package persistence.event.merge;

public interface MergeEventListener {

    void onMerge(MergeEvent event);
}
