package event.listener.load;

import builder.dml.EntityData;
import event.listener.EventListener;
import persistence.EntityLoader;

public class LoadEventListenerImpl<T> implements LoadEventListener<T> {

    private final EntityLoader entityLoader;

    public LoadEventListenerImpl(EntityLoader entityLoader) {
        this.entityLoader = entityLoader;
    }

    @Override
    public T onLoad(EntityData entityData) {
        return this.entityLoader.find(entityData);
    }

}
