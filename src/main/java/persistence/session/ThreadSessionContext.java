package persistence.session;

import persistence.entity.EntityManager;

import java.util.HashMap;
import java.util.Map;

public class ThreadSessionContext implements CurrentSessionContext {

    private final Map<Thread, EntityManager> sessionMap;

    private ThreadSessionContext(Map<Thread, EntityManager> sessionMap) {
        this.sessionMap = sessionMap;
    }

    public ThreadSessionContext() {
        this(new HashMap<>());
    }

    @Override
    public EntityManager currentEntityManager() {
        return sessionMap.get(Thread.currentThread());
    }

    @Override
    public void bindEntityManager(EntityManager entityManager) {
        sessionMap.put(Thread.currentThread(), entityManager);
    }

}
