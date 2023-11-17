package hibernate.event.merge;

public interface MergeEventListener {

    <T> void onMerge(MergeEvent<T> event);
}
