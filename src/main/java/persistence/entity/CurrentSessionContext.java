package persistence.entity;

import java.util.Optional;

public class CurrentSessionContext {
    private final ThreadLocal<EntityManager> sessionRegistry = new ThreadLocal<>();

    public void openSession(EntityManager entityManager) {
        sessionRegistry.set(entityManager);
    }

    public Optional<EntityManager> getSession() {
        return Optional.ofNullable(sessionRegistry.get());
    }

    public void closeSession() {
        sessionRegistry.remove();
    }
}
