package event.listener;

import builder.dml.EntityData;
import persistence.EntityLoader;

public class LoadEventListenerImpl<T> implements EventListener<T> {

    private final EntityLoader entityLoader;

    public LoadEventListenerImpl(EntityLoader entityLoader) {
        this.entityLoader = entityLoader;
    }

    @Override
    public T handleEvent(EntityData entityData) {
        return this.entityLoader.find(entityData);
    }
}
