package hibernate.entity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleCurrentSessionContext implements CurrentSessionContext {

    private final Map<Thread, EntityManager> entityManagerMap;

    public SimpleCurrentSessionContext(Map<Thread, EntityManager> entityManagerMap) {
        this.entityManagerMap = entityManagerMap;
    }

    public SimpleCurrentSessionContext() {
        this(new ConcurrentHashMap<>());
    }

    @Override
    public EntityManager currentSession() {
        Thread currentThread = Thread.currentThread();
        return entityManagerMap.get(currentThread);
    }
}
