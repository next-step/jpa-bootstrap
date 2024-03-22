package persistence.entity.event.save;

import persistence.entity.event.PersistEvent;

public class SaveEvent<T, ID> implements PersistEvent<T, ID> {

    private final ID id;
    private final T entity;

    public SaveEvent(ID id, T entity) {
        this.id = id;
        this.entity = entity;
    }

    @Override
    public ID getId() {
        return id;
    }

    @Override
    public T getEntity() {
        return entity;
    }
}
