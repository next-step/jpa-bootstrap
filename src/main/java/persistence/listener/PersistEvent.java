package persistence.listener;

public class PersistEvent<T> {
    private final T instance;

    public PersistEvent(T instance) {
        this.instance = instance;
    }

    public T getInstance() {
        return instance;
    }
}
