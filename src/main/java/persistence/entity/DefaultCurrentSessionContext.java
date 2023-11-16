package persistence.entity;

import jdbc.JdbcTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultCurrentSessionContext implements CurrentSessionContext {

    private final Map<Long, EntityManager> entityManagerMap;

    private DefaultCurrentSessionContext(Map<Long, EntityManager> entityManagerMap) {
        this.entityManagerMap = entityManagerMap;
    }

    public static DefaultCurrentSessionContext of(Thread thread, JdbcTemplate jdbcTemplate) {
        DefaultEntityManager entityManager = DefaultEntityManager.of(jdbcTemplate);
        ConcurrentHashMap<Long, EntityManager> entityManagerMap = new ConcurrentHashMap<>();
        entityManagerMap.put(thread.getId(), entityManager);
        return new DefaultCurrentSessionContext(entityManagerMap);
    }

    @Override
    public EntityManager currentSession() {
        Thread currentThread = Thread.currentThread();
        return entityManagerMap.get(currentThread.getId());
    }
}
