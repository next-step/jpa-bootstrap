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
        throw new IllegalStateException("현재 스레드에 존재하는 entitymanager 세션이 없습니다.");
    }
}
