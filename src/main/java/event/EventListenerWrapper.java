package event;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class EventListenerWrapper<EVENT_LISTENER> {
    private final EVENT_LISTENER eventListener;

    public EventListenerWrapper(EVENT_LISTENER eventListener) {
        this.eventListener = eventListener;
    }

    public <EVENT, RETURN> RETURN fireEventWithReturn(EVENT event, BiFunction<EVENT_LISTENER, EVENT, RETURN> action) {
        return action.apply(eventListener, event);
    }

    public <EVENT> void fireEvent(EVENT event, BiConsumer<EVENT_LISTENER, EVENT> action) {
        action.accept(eventListener, event);
    }
}
