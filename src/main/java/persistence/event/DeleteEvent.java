package persistence.event;

public class DeleteEvent {
    private final Object entity;

    public DeleteEvent(final Object entity) {
        this.entity = entity;
    }

    public Class<?> getTargetClass() {
        return entity.getClass();
    }
    public Object getTarget() {
        return entity;
    }
}
