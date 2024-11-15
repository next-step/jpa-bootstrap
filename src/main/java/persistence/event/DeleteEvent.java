package persistence.event;

import persistence.bootstrap.Metamodel;

public class DeleteEvent<T> {
    private final Metamodel metamodel;
    private final T entity;

    public DeleteEvent(Metamodel metamodel, T entity) {
        this.metamodel = metamodel;
        this.entity = entity;
    }

    public Metamodel getMetamodel() {
        return metamodel;
    }

    public T getEntity() {
        return entity;
    }
}
