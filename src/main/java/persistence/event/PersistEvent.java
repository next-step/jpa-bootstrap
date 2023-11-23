package persistence.event;

public class PersistEvent<T> implements Event<T> {
    private final T entity;

    public PersistEvent(final T entity) {
        this.entity = entity;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<T> getTargetClass() {
        return (Class<T>) entity.getClass();
    }

    @Override
    public T getTarget() {
        return entity;
    }

    @Override
    public Object getTargetId() {
        return null;
    }

    @Override
    public EventType getType() {
        return EventType.PERSIST;
    }
}
