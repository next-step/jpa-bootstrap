package persistence.sql.dml.impl;

import boot.MetaModel;
import boot.Metadata;
import event.EventListenerRegistry;
import event.impl.DefaultEventListenerRegistry;
import persistence.proxy.ProxyFactory;
import persistence.sql.context.impl.DefaultPersistenceContext;
import persistence.sql.dml.EntityManager;
import persistence.sql.dml.EntityManagerFactory;

public class DefaultEntityManagerFactory implements EntityManagerFactory {
    private static final ThreadLocal<EntityManager> CURRENT_ENTITY_MANAGER = new ThreadLocal<>();

    private final MetaModel metaModel;
    private final EventListenerRegistry registry;

    public DefaultEntityManagerFactory(MetaModel metaModel, EventListenerRegistry registry) {
        this.metaModel = metaModel;
        this.registry = registry;
    }

    public static DefaultEntityManagerFactory create(Metadata metadata, ProxyFactory proxyFactory, EventListenerRegistry registry) {
        MetaModel metaModel = MetaModel.newInstance(metadata, proxyFactory);
        return new DefaultEntityManagerFactory(metaModel, registry);
    }

    @Override
    public EntityManager entityManager() {
        EntityManager entityManager = CURRENT_ENTITY_MANAGER.get();

        if (entityManager != null) {
            return entityManager;
        }

        entityManager = new DefaultEntityManager(new DefaultPersistenceContext(), metaModel, registry);
        CURRENT_ENTITY_MANAGER.set(entityManager);

        return entityManager;
    }
}
