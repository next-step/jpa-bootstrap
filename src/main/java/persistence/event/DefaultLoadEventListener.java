package persistence.event;

import persistence.entity.loader.EntityLoader;
import persistence.entity.loader.EntityLoaders;
import persistence.entity.persister.EntityPersister;
import persistence.entity.persister.EntityPersisters;

public class DefaultLoadEventListener implements LoadEventListener {

    private final EntityLoaders entityLoaders;

    public DefaultLoadEventListener(final EntityLoaders entityLoaders) {
        this.entityLoaders = entityLoaders;
    }

    @Override
    public <T> T onLoad(final LoadEvent<T> loadEvent) {
        final EntityLoader<T> entityLoader = entityLoaders.getEntityLoader(loadEvent.getTargetClass());
        return entityLoader.loadById(loadEvent.getTarget()).orElse(null);
    }
}
