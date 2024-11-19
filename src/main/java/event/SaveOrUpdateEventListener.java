package event;

public interface SaveOrUpdateEventListener extends EventListener {
    void onSaveOrUpdate(SaveOrUpdateEvent event);
}
