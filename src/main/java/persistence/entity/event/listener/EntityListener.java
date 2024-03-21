package persistence.entity.event.listener;

import persistence.entity.event.EntityEvent;
import persistence.entity.event.action.ActionQueue;

public interface EntityListener {

    <T> Object handleEvent(EntityEvent<T> event, ActionQueue actionQueue);

}
