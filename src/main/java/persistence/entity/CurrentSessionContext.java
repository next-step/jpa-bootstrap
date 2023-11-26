package persistence.entity;

public class CurrentSessionContext {
    private final ThreadLocal<EntityManager> entityManagerThreadLocal;

    public CurrentSessionContext() {
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
