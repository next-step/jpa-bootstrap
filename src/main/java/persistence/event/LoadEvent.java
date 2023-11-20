package persistence.event;

public class LoadEvent<T> {
    private final Object id;
    private final Class<T> clazz;

    public LoadEvent(final Object id, final Class<T> clazz) {
        this.id = id;
        this.clazz = clazz;
    }

    public Class<T> getTargetClass() {
        return this.clazz;
    }
    public Object getTarget() {
        return id;
    }
}
