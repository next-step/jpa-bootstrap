package persistence.entity;

public class ThreadLocalSessionContext implements CurrentSessionContext {

    private final ThreadLocal<EntityManager> entityManagerThreadLocal;

    public ThreadLocalSessionContext() {
        entityManagerThreadLocal = new ThreadLocal<>();
    }

    public void bind(EntityManager entityManager) {
        entityManagerThreadLocal.set(entityManager);
    }

    public EntityManager currentSession() {
        return entityManagerThreadLocal.get();
    }

    public void closeCurrentSession() {
        entityManagerThreadLocal.remove();
    }

}
