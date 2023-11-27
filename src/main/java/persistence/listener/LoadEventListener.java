package persistence.listener;

@FunctionalInterface
public interface LoadEventListener {

    void onLoad(LoadEvent eventType);

}
