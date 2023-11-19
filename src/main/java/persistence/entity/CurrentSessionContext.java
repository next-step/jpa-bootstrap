package persistence.entity;

public class CurrentSessionContext {

    private static final ThreadLocal<EntityManager> entityManagerThreadLocal = new ThreadLocal<>();

    private CurrentSessionContext() {
    }

    public static void openSession(EntityManager entityManager) {
        entityManagerThreadLocal.set(entityManager);
    }

    public static EntityManager currentSession() {
        return entityManagerThreadLocal.get();
    }

    public static void close() {
        entityManagerThreadLocal.remove();
    }
}
