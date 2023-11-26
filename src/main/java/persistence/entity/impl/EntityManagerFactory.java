package persistence.entity.impl;

import java.sql.Connection;
import persistence.entity.EntityManager;
import persistence.entity.PersistenceContext;
import persistence.entity.impl.context.DefaultPersistenceContext;
import persistence.entity.impl.event.EntityEventDispatcher;
import persistence.entity.impl.event.dispatcher.EntityEventDispatcherImpl;
import persistence.entity.impl.event.listener.DeleteEntityEventListenerImpl;
import persistence.entity.impl.event.listener.LoadEntityEventListenerImpl;
import persistence.entity.impl.event.listener.MergeEntityEventListenerImpl;
import persistence.entity.impl.event.listener.PersistEntityEventListenerImpl;
import persistence.entity.impl.event.publisher.EntityEventPublisherImpl;
import persistence.entity.impl.retrieve.EntityLoader;
import persistence.entity.impl.retrieve.EntityLoaderImpl;
import persistence.entity.impl.store.EntityPersister;
import persistence.entity.impl.store.EntityPersisterImpl;
import registry.EntityMetaRegistry;

/**
 * EntityManagerFactory는 JPA의 EntityManagerFactory 인터페이스를 구현예정입니다.
 */
public class EntityManagerFactory {

    private final Connection connection;
    private final EntityMetaRegistry entityMetaRegistry;

    public EntityManagerFactory(Connection connection, EntityMetaRegistry entityMetaRegistry) {
        this.connection = connection;
        this.entityMetaRegistry = entityMetaRegistry;
    }

    public EntityManager createEntityManager() {
        final EntityEventDispatcher entityEventDispatcher = initEventDispatcher(connection);
        final PersistenceContext persistenceContext = new DefaultPersistenceContext(entityMetaRegistry);

        return new EntityManagerImpl(
            connection,
            persistenceContext,
            initEventPublisher(entityEventDispatcher),
            entityMetaRegistry
        );
    }

    private EntityEventPublisherImpl initEventPublisher(EntityEventDispatcher entityEventDispatcher) {
        return new EntityEventPublisherImpl(entityEventDispatcher);
    }

    private EntityEventDispatcherImpl initEventDispatcher(Connection connection) {
        final EntityLoader entityLoader = new EntityLoaderImpl(connection, entityMetaRegistry);
        final EntityPersister entityPersister = new EntityPersisterImpl(connection, entityMetaRegistry);

        return new EntityEventDispatcherImpl(
            new LoadEntityEventListenerImpl(entityLoader),
            new MergeEntityEventListenerImpl(entityPersister),
            new PersistEntityEventListenerImpl(entityPersister),
            new DeleteEntityEventListenerImpl(entityPersister)
        );
    }

}
