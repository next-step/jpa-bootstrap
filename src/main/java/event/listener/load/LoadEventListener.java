package event.listener.load;

import builder.dml.EntityData;

public interface LoadEventListener<T> {
    T onLoad(EntityData entityData);
}
