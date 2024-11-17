package persistence.entity.manager;

import java.util.Optional;

public class CurrentSessionContext {
    public static final String SESSION_ALREADY_CREATED_MESSAGE = "세션이 이미 생성되어 있습니다.";

    private final ThreadLocal<EntityManager> sessionRegistry = new ThreadLocal<>();

    public void openSession(EntityManager entityManager) {
        if (isOpened()) {
            throw new IllegalStateException(SESSION_ALREADY_CREATED_MESSAGE);
        }
        sessionRegistry.set(entityManager);
    }

    public Optional<EntityManager> getSession() {
        return Optional.ofNullable(sessionRegistry.get());
    }

    public void closeSession() {
        final EntityManager entityManager = sessionRegistry.get();
        if (entityManager != null) {
            entityManager.clear();
        }
        sessionRegistry.remove();
    }

    private boolean isOpened() {
        return sessionRegistry.get() != null;
    }
}
