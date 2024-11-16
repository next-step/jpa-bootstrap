package event.listener;

import builder.dml.EntityData;

public interface EventListener<T> {
    T handleEvent(EntityData entityData);
}
