package persistence.entity.manager.factory;

import persistence.bootstrap.Metamodel;
import persistence.entity.manager.CurrentSessionContext;
import persistence.entity.manager.DefaultEntityManager;
import persistence.entity.manager.EntityManager;

public class EntityManagerFactory {
    private static volatile EntityManagerFactory instance;

    private final CurrentSessionContext currentSessionContext;
    private final Metamodel metamodel;

    public EntityManagerFactory(CurrentSessionContext currentSessionContext, Metamodel metamodel) {
        this.currentSessionContext = currentSessionContext;
        this.metamodel = metamodel;
    }

    public EntityManager openSession() {
        return currentSessionContext.getSession()
                .orElseGet(this::createEntityManager);
    }

    public void closeSession() {
        currentSessionContext.closeSession();
    }

    private EntityManager createEntityManager() {
        final EntityManager entityManager = new DefaultEntityManager(metamodel);
        currentSessionContext.openSession(entityManager);
        return entityManager;
    }
}
