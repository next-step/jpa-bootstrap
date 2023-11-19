package persistence.entity;

public class DefaultCurrentSessionContext {

    private static final ThreadLocal<EntityManager> entityManagerThreadLocal = new ThreadLocal<>();

    private DefaultCurrentSessionContext() {
    }

    public static void openSession(EntityManager entityManager) {
        entityManagerThreadLocal.set(entityManager);
    }

    public static EntityManager currentSession() {
        return entityManagerThreadLocal.get();
    }
}
