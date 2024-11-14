package persistence.event;

public abstract class AbstractEvent {

    private final EventSource session;

    public AbstractEvent(EventSource source) {
        this.session = source;
    }

    public EventSource getSession() {
        return session;
    }
}
