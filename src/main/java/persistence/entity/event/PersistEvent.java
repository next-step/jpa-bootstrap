package persistence.entity.event;

public interface PersistEvent<T, ID> {

    ID getId();

    T getEntity();
}
