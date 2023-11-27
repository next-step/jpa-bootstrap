package persistence.entity.manager;

public class CurrentSessionContext {
    private static final ThreadLocal<EntityManager> entityManagerThreadLocal = new ThreadLocal<>();

    public static void clearSession() {
        EntityManager entityManager = entityManagerThreadLocal.get();
        if (entityManager != null) {
            entityManagerThreadLocal.remove();
        }
    }


    public EntityManager getSession() {
        return entityManagerThreadLocal.get();
    }

    public void setSession(EntityManager entityManager) {
        entityManagerThreadLocal.set(entityManager);
    }
}
