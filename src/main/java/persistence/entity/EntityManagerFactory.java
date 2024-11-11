package persistence.entity;

import persistence.bootstrap.Metamodel;

public class EntityManagerFactory {
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
