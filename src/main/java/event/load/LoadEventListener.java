package event.load;

public interface LoadEventListener {

    <T> T onLoad(LoadEvent<T> event);
}
