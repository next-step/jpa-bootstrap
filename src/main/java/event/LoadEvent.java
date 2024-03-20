package event;

public class LoadEvent<T>{

    private final Class<T> clazz;
    private final Object id;

    public LoadEvent(Class<T> clazz, Object id) {
        this.clazz = clazz;
        this.id = id;
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public Object getId() {
        return id;
    }
}
