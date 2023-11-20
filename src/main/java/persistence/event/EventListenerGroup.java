package persistence.event;


import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class EventListenerGroup<EVENT_LISTENER extends EventListener> {

    private final EVENT_LISTENER eventListener;

    public EventListenerGroup(final EVENT_LISTENER eventListener) {
        this.eventListener = eventListener;
    }

    public <EVENT> void fireEvent(final EVENT event, final BiConsumer<EVENT_LISTENER, EVENT> biConsumer) {
        biConsumer.accept(eventListener, event);
    }

    public <EVENT, RETURN> RETURN fireEventReturn(final EVENT event, final BiFunction<EVENT_LISTENER, EVENT, RETURN> biFunction) {
        return biFunction.apply(eventListener, event);
    }
}
