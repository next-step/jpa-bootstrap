package persistence.entity.manager;

import java.util.Objects;
import java.util.Optional;

public class CurrentSessionContext {
    private final ThreadLocal<EntityManager> sessionRegistry = new ThreadLocal<>();

    public synchronized EntityManager openSession(EntityManager entityManager) {
        if (isOpened()) {
            return sessionRegistry.get();
        }
        sessionRegistry.set(entityManager);
        return entityManager;
    }

    public Optional<EntityManager> getSession() {
        return Optional.ofNullable(sessionRegistry.get());
    }

    public void closeSession() {
        final EntityManager entityManager = sessionRegistry.get();
        if (Objects.nonNull(entityManager)) {
            entityManager.clear();
        }
        sessionRegistry.remove();
    }

    private boolean isOpened() {
        return Objects.nonNull(sessionRegistry.get());
    }
}
