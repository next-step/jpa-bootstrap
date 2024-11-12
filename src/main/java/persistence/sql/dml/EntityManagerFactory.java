package persistence.sql.dml;

import boot.MetaModel;
import boot.Metadata;
import persistence.proxy.ProxyFactory;
import persistence.sql.context.impl.DefaultPersistenceContext;
import persistence.sql.dml.impl.DefaultEntityManager;

public class EntityManagerFactory {
    private static final ThreadLocal<EntityManager> CURRENT_ENTITY_MANAGER = new ThreadLocal<>();

    private final MetaModel metaModel;

    public EntityManagerFactory(MetaModel metaModel) {
        this.metaModel = metaModel;
    }

    public static EntityManagerFactory create(Metadata metadata, ProxyFactory proxyFactory) {
        MetaModel metaModel = MetaModel.newInstance(metadata, proxyFactory);
        return new EntityManagerFactory(metaModel);
    }

    public EntityManager entityManager() {
        return entityManager(true);
    }

    public EntityManager entityManager(boolean create) {
        EntityManager entityManager = CURRENT_ENTITY_MANAGER.get();

        if (entityManager != null) {
            return entityManager;
        }

        if (!create) {
            return null;
        }

        entityManager = new DefaultEntityManager(new DefaultPersistenceContext(), metaModel);
        CURRENT_ENTITY_MANAGER.set(entityManager);

        return entityManager;
    }
}
