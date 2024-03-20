package persistence.session;

import persistence.entity.EntityManager;

import java.util.HashMap;
import java.util.Map;

public class ThreadSessionContext implements CurrentSessionContext {

    private static final Map<Thread, EntityManager> SESSION_MAP = new HashMap<>();

    @Override
    public EntityManager currentEntityManager() {
        return SESSION_MAP.get(Thread.currentThread());
    }

    @Override
    public void bindEntityManager(EntityManager entityManager) {
        SESSION_MAP.put(Thread.currentThread(), entityManager);
    }

}
