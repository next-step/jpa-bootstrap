package hibernate.event.delete;

public class DeleteEvent<T> {

    private final T entity;
    private final Class<T> clazz;

    private DeleteEvent(final T entity, final Class<T> clazz) {
        this.entity = entity;
        this.clazz = clazz;
    }

    public static <T> DeleteEvent<T> createEvent(final T entity) {
        return new DeleteEvent<>(entity, (Class<T>) entity.getClass());
    }

    public T getEntity() {
        return entity;
    }

    public Class<T> getClazz() {
        return clazz;
    }
}
