package persistence.entity.event;

public interface PersistEventListener {

    <T, ID> void fireEvent(PersistEvent<T, ID> event);

}
