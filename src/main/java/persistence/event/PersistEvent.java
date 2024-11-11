package persistence.event;

public class PersistEvent extends AbstractEvent {

    private final Object entity;

    private PersistEvent(EventSource source, Object entity) {
        super(source);
        this.entity = entity;
    }

    public static PersistEvent create(EventSource source, Object entity) {
        return new PersistEvent(source, entity);
    }

    public Object getEntity() {
        return entity;
    }
}
