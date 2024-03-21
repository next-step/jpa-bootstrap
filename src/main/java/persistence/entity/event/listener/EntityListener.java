package persistence.entity.event.listener;

import persistence.entity.event.EntityEvent;

public interface EntityListener {

    <T> Object handleEvent(EntityEvent<T> event);

}
