package persistence.event.flush;

import persistence.event.AbstractEvent;
import persistence.event.EventSource;

public class FlushEvent extends AbstractEvent {

    public FlushEvent(EventSource source) {
        super(source);
    }
}
