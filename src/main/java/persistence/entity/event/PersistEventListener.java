package persistence.entity.event;

public interface PersistEventListener extends EventListener {

    <T, ID> void fireEvent(PersistEvent<T, ID> event);

}
