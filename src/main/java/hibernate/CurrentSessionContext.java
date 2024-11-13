package hibernate;

import persistence.EntityManager;

public class CurrentSessionContext {

    private static final ThreadLocal<EntityManager> currentEntityManager = new ThreadLocal<>();

    public void bind(EntityManager entityManager) {
        currentEntityManager.set(entityManager);
    }

    public void unbind() {
        currentEntityManager.remove();
    }

    public EntityManager getCurrentEntityManager() {
        EntityManager entityManager = currentEntityManager.get();
        if (entityManager == null) {
            throw new IllegalStateException("No EntityManager bound to this thread.");
        }
        return entityManager;
    }
}
