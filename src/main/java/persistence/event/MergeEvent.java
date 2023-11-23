package persistence.event;

public class MergeEvent<T> implements Event<T> {
    private final T entity;
    private final Object id;

    public MergeEvent(final T entity, final Object id) {
        this.entity = entity;
        this.id = id;
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
        return id;
    }

    @Override
    public EventType getType() {
        return EventType.MERGE;
    }
}
