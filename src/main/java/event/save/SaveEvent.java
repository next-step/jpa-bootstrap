package event.save;

public class SaveEvent<T> {

    private final T entity;
    private final Class<T> clazz;

    public SaveEvent(T entity) {
        this.entity = entity;
        this.clazz = (Class<T>) entity.getClass();
    }

    public T getEntity() {
        return entity;
    }

    public Class<T> getClazz() {
        return clazz;
    }
}
