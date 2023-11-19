package persistence.listener;

@FunctionalInterface
public interface PersistEventListener {
    <T> T onPersist(PersistEvent<T> persistEvent);
}
