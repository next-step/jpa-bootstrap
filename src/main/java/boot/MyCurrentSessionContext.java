package boot;

import persistence.entity.EntityManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MyCurrentSessionContext implements CurrentSessionContext {
    private final Map<Thread, EntityManager> sessions;

    public MyCurrentSessionContext() {
        this.sessions = new ConcurrentHashMap<>();
    }

    @Override
    public EntityManager currentSession() {
        Thread thread = Thread.currentThread();
        return sessions.get(thread);
    }

    @Override
    public void bindSession(EntityManager entityManager) {
        Thread thread = Thread.currentThread();
        sessions.put(thread, entityManager);
    }
}
