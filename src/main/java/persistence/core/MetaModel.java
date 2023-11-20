package persistence.core;

import persistence.entity.loader.EntityLoaders;
import persistence.entity.persister.EntityPersisters;
import persistence.entity.proxy.EntityProxyFactory;
import persistence.event.EventListenerGroup;

import java.util.Set;

public interface MetaModel {
    <T> EntityMetadata<T> getEntityMetadata(Class<T> clazz);

    Set<EntityMetadata<?>> getOneToManyAssociatedEntitiesMetadata(EntityMetadata<?> entityMetadata);

    EntityPersisters getEntityPersisters();

    EntityLoaders getEntityLoaders();

    EntityProxyFactory getEntityProxyFactory();

    EventListenerGroup getEventListenerGroup();
}
