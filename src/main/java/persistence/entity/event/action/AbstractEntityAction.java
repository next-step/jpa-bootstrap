package persistence.entity.event.action;

import persistence.entity.persister.EntityPersister;

public abstract class AbstractEntityAction<T> implements EntityAction {

    protected EntityPersister<T> persister;
    protected T entity;

    protected AbstractEntityAction(EntityPersister<T> persister, T entity) {
        this.persister = persister;
        this.entity = entity;
    }
}
