package persistence.event.load;

public interface LoadEventListener {

    <T> void onLoad(LoadEvent<T> event);
}
