package persistence.entity.event;

public interface EventListener {

	<T, ID> void fireEvent(PersistEvent<T, ID> event);
}
