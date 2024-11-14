package persistence.event;

public interface LoadEventListener {
    <T> void onLoad(LoadEvent<T> loadEvent);
}
