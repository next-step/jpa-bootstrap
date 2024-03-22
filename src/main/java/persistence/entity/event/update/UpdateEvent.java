package persistence.entity.event.update;

import persistence.entity.event.PersistEvent;

public class UpdateEvent<T, ID> implements PersistEvent<T, ID> {

    private final ID id;
    private final T entity;

    public UpdateEvent(ID id, T entity) {
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
