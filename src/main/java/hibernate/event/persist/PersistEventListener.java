package hibernate.event.persist;

public interface PersistEventListener {

    <T> Object onPersist(PersistEvent<T> event);
}
