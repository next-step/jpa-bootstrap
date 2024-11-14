package persistence.sql.dml.impl;

import boot.MetaModel;
import boot.Metadata;
import persistence.proxy.ProxyFactory;
import persistence.sql.context.impl.DefaultPersistenceContext;
import persistence.sql.dml.EntityManager;
import persistence.sql.dml.EntityManagerFactory;

public class DefaultEntityManagerFactory implements EntityManagerFactory {
    private static final ThreadLocal<EntityManager> CURRENT_ENTITY_MANAGER = new ThreadLocal<>();

    private final MetaModel metaModel;

    public DefaultEntityManagerFactory(MetaModel metaModel) {
        this.metaModel = metaModel;
    }

    public static DefaultEntityManagerFactory create(Metadata metadata, ProxyFactory proxyFactory) {
        MetaModel metaModel = MetaModel.newInstance(metadata, proxyFactory);
        return new DefaultEntityManagerFactory(metaModel);
    }

    @Override
    public EntityManager entityManager() {
        EntityManager entityManager = CURRENT_ENTITY_MANAGER.get();

        if (entityManager != null) {
            return entityManager;
        }

        entityManager = new DefaultEntityManager(new DefaultPersistenceContext(), metaModel);
        CURRENT_ENTITY_MANAGER.set(entityManager);

        return entityManager;
    }
}
