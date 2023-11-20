package persistence.core;

import persistence.entity.loader.EntityLoaders;
import persistence.entity.persister.EntityPersisters;
import persistence.entity.proxy.EntityProxyFactory;
import persistence.event.EventListenerGroup;

import java.util.Set;

public class MetaModelImpl implements MetaModel {
    private final EntityMetadataProvider entityMetadataProvider;
    private final EntityPersisters entityPersisters;
    private final EntityLoaders entityLoaders;
    private final EntityProxyFactory entityProxyFactory;
    private final EventListenerGroup eventListenerGroup;

    public MetaModelImpl(final EntityMetadataProvider entityMetadataProvider, final EntityPersisters entityPersisters, final EntityLoaders entityLoaders) {
        this.entityMetadataProvider = entityMetadataProvider;
        this.entityPersisters = entityPersisters;
        this.entityLoaders = entityLoaders;
        this.entityProxyFactory = new EntityProxyFactory(entityLoaders);
        this.eventListenerGroup = new EventListenerGroup(entityPersisters, entityLoaders);
    }

    @Override
    public <T> EntityMetadata<T> getEntityMetadata(final Class<T> clazz) {
        return entityMetadataProvider.getEntityMetadata(clazz);
    }

    @Override
    public Set<EntityMetadata<?>> getOneToManyAssociatedEntitiesMetadata(final EntityMetadata<?> entityMetadata) {
        return entityMetadataProvider.getOneToManyAssociatedEntitiesMetadata(entityMetadata);
    }

    @Override
    public EntityPersisters getEntityPersisters() {
        return entityPersisters;
    }

    @Override
    public EntityLoaders getEntityLoaders() {
        return entityLoaders;
    }

    @Override
    public EntityProxyFactory getEntityProxyFactory() {
        return entityProxyFactory;
    }

    @Override
    public EventListenerGroup getEventListenerGroup() {
        return eventListenerGroup;
    }
}
