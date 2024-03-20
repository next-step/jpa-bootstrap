package event;

public interface LoadEventListener extends EventListener {

    <T> T onLoad(LoadEvent<T> loadEvent);
}
