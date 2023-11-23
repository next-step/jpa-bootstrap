package persistence.event;

import persistence.entity.loader.EntityLoader;
import persistence.entity.loader.EntityLoaders;

public class LoadEventListener implements EventListener {

    private final EntityLoaders entityLoaders;

    public LoadEventListener(final EntityLoaders entityLoaders) {
        this.entityLoaders = entityLoaders;
    }

    @Override
    public <T> T on(final Event<T> event) {
        final EntityLoader<T> entityLoader = entityLoaders.getEntityLoader(event.getTargetClass());
        return entityLoader.loadById(event.getTargetId()).orElse(null);
    }
}
