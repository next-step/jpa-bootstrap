package persistence.event;

public interface MergeEventListener {

    void onMerge(MergeEvent event);
}
