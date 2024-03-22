package persistence.entity.event.load;

public interface LoadEventListener {

    <ID, T> T onLoad(LoadEvent<ID> event);
}
