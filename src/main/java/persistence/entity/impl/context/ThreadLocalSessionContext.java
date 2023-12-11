package persistence.entity.impl.context;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import persistence.entity.EntityManager;
import persistence.entity.SessionContext;
import persistence.entity.impl.EntityManagerFactory;

public class ThreadLocalSessionContext implements SessionContext {

    private static ThreadLocal<Map<EntityManagerFactory, EntityManager>> SESSION_CONTEXT = ThreadLocal.withInitial(HashMap::new);

    private ThreadLocalSessionContext() {
    }

    public static ThreadLocalSessionContext init(EntityManagerFactory entityManagerFactory, Function<EntityManagerFactory, EntityManager> generateEntityManagerFunction) {
        final ThreadLocalSessionContext threadLocalSessionContext = new ThreadLocalSessionContext();
        threadLocalSessionContext.tryBindEntityManager(entityManagerFactory, generateEntityManagerFunction);
        return threadLocalSessionContext;
    }

    @Override
    public EntityManager tryBindEntityManager(EntityManagerFactory entityManagerFactory, Function<EntityManagerFactory, EntityManager> generateEntityManagerFunction) {
        if (generateEntityManagerFunction == null) {
            throw new RuntimeException("EntityManagerFactory에 해당하는 EntityManager를 매핑할 수 없습니다.");
        }

        return getEntityManagerMap()
            .computeIfAbsent(entityManagerFactory, generateEntityManagerFunction);
    }

    private Map<EntityManagerFactory, EntityManager> getEntityManagerMap() {
        return SESSION_CONTEXT.get();
    }
}
