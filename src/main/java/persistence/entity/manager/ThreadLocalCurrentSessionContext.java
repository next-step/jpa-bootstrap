package persistence.entity.manager;

import persistence.exception.CurrentSessionAlreadyOpenException;

import java.util.Objects;
import java.util.Optional;

public class ThreadLocalCurrentSessionContext implements CurrentSessionContext {
    private final ThreadLocal<EntityManager> currentSession;

    public ThreadLocalCurrentSessionContext() {
        this.currentSession = new ThreadLocal<>();
    }

    @Override
    public void open(final EntityManager entityManager) {
        if (Objects.nonNull(currentSession.get())) {
            throw new CurrentSessionAlreadyOpenException();
        }
        currentSession.set(entityManager);
    }

    @Override
    public Optional<EntityManager> getCurrentSession() {
        return Optional.ofNullable(currentSession.get());
    }

    @Override
    public void close() {
        currentSession.remove();
    }

}
