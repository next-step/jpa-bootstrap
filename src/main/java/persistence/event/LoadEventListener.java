package persistence.event;

public interface LoadEventListener {
    <T> T onLoad(LoadEvent<T> loadEvent);
}
