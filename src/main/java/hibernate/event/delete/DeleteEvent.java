package hibernate.event.delete;

import hibernate.action.ActionQueue;
import hibernate.action.DeleteActionQueue;

public class DeleteEvent<T> {

    private final DeleteActionQueue actionQueue;
    private final T entity;
    private final Class<T> clazz;

    private DeleteEvent(final DeleteActionQueue actionQueue, final T entity, final Class<T> clazz) {
        this.actionQueue = actionQueue;
        this.entity = entity;
        this.clazz = clazz;
    }

    public static <T> DeleteEvent<T> createEvent(final ActionQueue actionQueue, final T entity) {
        return new DeleteEvent<>(actionQueue, entity, (Class<T>) entity.getClass());
    }

    public DeleteActionQueue getActionQueue() {
        return actionQueue;
    }

    public T getEntity() {
        return entity;
    }

    public Class<T> getClazz() {
        return clazz;
    }
}
