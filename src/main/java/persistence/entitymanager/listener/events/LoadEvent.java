package persistence.entitymanager.listener.events;

import persistence.entity.context.PersistenceContext;

public class LoadEvent implements Event {
    private final Class<?> entityClass;
    private final Long id;
    private final PersistenceContext persistenceContext;
    private Object result;

    public LoadEvent(Class<?> entityClass, Long id, PersistenceContext persistenceContext) {
        this.entityClass = entityClass;
        this.id = id;
        this.persistenceContext = persistenceContext;

        this.result = null;
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public Long getId() {
        return id;
    }

    public PersistenceContext getPersistenceContext() {
        return persistenceContext;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public Object getResult() {
        return result;
    }
}
