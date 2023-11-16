package persistence.entity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultCurrentSessionContext implements CurrentSessionContext {

    private static DefaultCurrentSessionContext INSTANCE;

    private final Map<Long, EntityManager> entityManagerMap = new ConcurrentHashMap<>();

    private DefaultCurrentSessionContext() {
    }

    public static DefaultCurrentSessionContext getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DefaultCurrentSessionContext();
        }
        return INSTANCE;
    }

    @Override
    public void openSession(Thread thread, EntityManager entityManager) {
        entityManagerMap.put(thread.getId(), entityManager);
    }

    @Override
    public EntityManager currentSession() {
        Thread currentThread = Thread.currentThread();
        return entityManagerMap.get(currentThread.getId());
    }
}
