package event.listener;

import builder.dml.EntityData;
import event.action.ActionQueue;

public interface EventListener<T> {
    T handleEvent(EntityData entityData);
}
