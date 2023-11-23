package persistence.event;

public interface EventListener {
    void onPersist(PersistEvent persistEvent);

    void onMerge(MergeEvent mergeEvent);

    void onDelete(DeleteEvent deleteEvent);

    <T> T onLoad(LoadEvent<T> loadEvent);
}
