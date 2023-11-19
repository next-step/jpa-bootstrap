package persistence.event;

public class PersistEvent {
    private final Object entity;

    public PersistEvent(final Object entity) {
        this.entity = entity;
    }

    public Class<?> getTargetClass() {
        return entity.getClass();
    }
    public Object getTarget() {
        return entity;
    }
}
