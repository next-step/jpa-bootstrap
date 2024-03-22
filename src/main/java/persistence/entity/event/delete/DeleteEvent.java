package persistence.entity.event.delete;


import persistence.entity.event.PersistEvent;

public class DeleteEvent<T, ID> implements PersistEvent<T, ID> {
    private final ID id;
    private final T entity;

    public DeleteEvent(ID id, T entity) {
        this.id = id;
        this.entity = entity;
    }

    public ID getId() {
        return id;
    }

    public T getEntity() {
        return entity;
    }
}
