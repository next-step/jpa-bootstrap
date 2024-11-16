package event;

import boot.Metamodel;
import builder.dml.EntityData;
import event.action.ActionQueue;
import persistence.EntityLoader;

public class EventListenerRegistry<T> {

    private final EventListenerGroup<?> eventListenerGroup;

    public EventListenerRegistry(Metamodel metamodel, EntityLoader entityLoader) {
        eventListenerGroup = new EventListenerGroup<>(metamodel, entityLoader);
    }

    @SuppressWarnings("unchecked")
    public T handleEvent(EventType eventType, EntityData entityData) {
        return (T) eventListenerGroup.handleEvent(eventType, entityData);
    }

    public void execute() {
        this.eventListenerGroup.execute();
    }

    public EventListenerRegistry<T> addActionQueue(ActionQueue actionQueue) {
        this.eventListenerGroup.setActionQueue(actionQueue);
        return this;
    }

}
