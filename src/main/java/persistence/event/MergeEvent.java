package persistence.event;

public class MergeEvent {
    private final Object entity;

    public MergeEvent(final Object entity) {
        this.entity = entity;
    }

    public Class<?> getTargetClass() {
        return entity.getClass();
    }
    public Object getTarget() {
        return entity;
    }
}
