package event.save;

public interface SaveEventListener {

    <T> void onSave(SaveEvent<T> event);
}
