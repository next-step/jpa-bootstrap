package event.load;

import builder.dml.EntityData;
import persistence.EntityLoader;

public class LoadEventListenerImpl<T> implements LoadEventListener<T> {

    private final EntityLoader entityLoader;

    public LoadEventListenerImpl(EntityLoader entityLoader) {
        this.entityLoader = entityLoader;
    }

    @Override
    public T onLoad(EntityData entityData) {
        return (T) this.entityLoader.find(entityData);
    }
}
