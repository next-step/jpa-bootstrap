package boot;

import persistence.entity.EntityManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MyEntityManagerContext implements EntityManagerContext {
    private final Map<Thread, EntityManager> sessions;

    public MyEntityManagerContext() {
        this.sessions = new ConcurrentHashMap<>();
    }

    @Override
    public EntityManager currentEntityManager() {
        Thread thread = Thread.currentThread();
        return sessions.get(thread);
    }

    @Override
    public void bindEntityManager(EntityManager entityManager) {
        Thread thread = Thread.currentThread();
        sessions.put(thread, entityManager);
    }
}
