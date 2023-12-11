package persistence.entity.impl;

import jakarta.persistence.FlushModeType;
import java.sql.Connection;
import persistence.entity.EntityManager;
import persistence.entity.PersistenceContext;
import persistence.entity.SessionContext;
import persistence.entity.impl.context.DefaultPersistenceContext;
import persistence.entity.impl.context.ThreadLocalSessionContext;
import persistence.entity.impl.event.EntityEventDispatcher;
import persistence.entity.impl.event.EntityEventPublisher;
import persistence.entity.impl.event.dispatcher.EntityEventDispatcherImpl;
import persistence.entity.impl.event.listener.DeleteEntityEventListenerImpl;
import persistence.entity.impl.event.listener.FlushEntityEventListenerImpl;
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

    private final SessionContext sessionContext;
    private final Connection connection;
    private final EntityMetaRegistry entityMetaRegistry;

    public EntityManagerFactory(Connection connection, EntityMetaRegistry entityMetaRegistry) {
        this.connection = connection;
        this.entityMetaRegistry = entityMetaRegistry;
        this.sessionContext = initSessionContext(connection, entityMetaRegistry);
    }

    private SessionContext initSessionContext(Connection connection, EntityMetaRegistry entityMetaRegistry) {
        return ThreadLocalSessionContext.init(this, entityManagerFactory ->
            buildEntityManager(connection, entityMetaRegistry)
        );
    }

    public EntityManager openSession() {
        return sessionContext.tryBindEntityManager(this, entityManagerFactory ->
            buildEntityManager(connection, entityMetaRegistry)
        );
    }

    private EntityManager buildEntityManager(Connection connection, EntityMetaRegistry entityMetaRegistry) {
        final EntityEventDispatcher entityEventDispatcher = initEventDispatcher(connection);
        final EntityEventPublisher entityEventPublisher = initEventPublisher(entityEventDispatcher);
        final PersistenceContext persistenceContext = new DefaultPersistenceContext(entityMetaRegistry);

        return EntityManagerImpl.of(connection, persistenceContext, entityEventPublisher, entityMetaRegistry, FlushModeType.AUTO);
    }

    private EntityEventPublisher initEventPublisher(EntityEventDispatcher entityEventDispatcher) {
        return new EntityEventPublisherImpl(entityEventDispatcher);
    }

    private EntityEventDispatcher initEventDispatcher(Connection connection) {
        final EntityLoader entityLoader = new EntityLoaderImpl(connection, entityMetaRegistry);
        final EntityPersister entityPersister = new EntityPersisterImpl(connection, entityMetaRegistry);

        return new EntityEventDispatcherImpl(
            new LoadEntityEventListenerImpl(entityLoader),
            new MergeEntityEventListenerImpl(entityPersister),
            new PersistEntityEventListenerImpl(entityPersister),
            new DeleteEntityEventListenerImpl(entityPersister),
            new FlushEntityEventListenerImpl()
        );
    }

}
