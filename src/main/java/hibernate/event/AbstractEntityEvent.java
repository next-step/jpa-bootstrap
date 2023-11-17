package hibernate.event;

import hibernate.action.ActionQueue;

public abstract class AbstractEntityEvent {

    private final ActionQueue actionQueue;

    public AbstractEntityEvent(ActionQueue actionQueue) {
        this.actionQueue = actionQueue;
    }
}
