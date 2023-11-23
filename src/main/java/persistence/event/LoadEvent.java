package persistence.event;

public class LoadEvent<T> implements Event<T> {
    private final Object id;
    private final T entity;
    private final Class<T> clazz;

    public LoadEvent(final Object id, final Class<T> clazz) {
        this.id = id;
        this.clazz = clazz;
        this.entity = null;
    }

    @Override
    public Class<T> getTargetClass() {
        return this.clazz;
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
        return EventType.LOAD;
    }


}
