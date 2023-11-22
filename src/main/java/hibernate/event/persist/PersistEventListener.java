package hibernate.event.persist;

public interface PersistEventListener {

    <T> void onPersist(PersistEvent<T> event);
}
