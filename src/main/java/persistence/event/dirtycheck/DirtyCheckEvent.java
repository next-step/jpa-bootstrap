package persistence.event.dirtycheck;

import persistence.bootstrap.Metamodel;
import persistence.meta.EntityColumn;

import java.util.List;

public class DirtyCheckEvent<T> {
    private final Metamodel metamodel;
    private final T entity;
    private final Object snapshot;
    private List<EntityColumn> result;

    public DirtyCheckEvent(Metamodel metamodel, T entity, Object snapshot) {
        this.metamodel = metamodel;
        this.entity = entity;
        this.snapshot = snapshot;
    }

    public Metamodel getMetamodel() {
        return metamodel;
    }

    public T getEntity() {
        return entity;
    }

    public Object getSnapshot() {
        return snapshot;
    }

    public List<EntityColumn> getResult() {
        return result;
    }

    public void setResult(List<EntityColumn> result) {
        this.result = result;
    }
}
