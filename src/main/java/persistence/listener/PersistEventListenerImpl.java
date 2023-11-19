package persistence.listener;

import persistence.entity.manager.EntityManager;

public class PersistEventListenerImpl implements PersistEventListener {
    private final EntityManager entityManager;

    public PersistEventListenerImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public <T> T onPersist(PersistEvent<T> persistEvent) {
        return entityManager.getPersistenceContext().addEntity(persistEvent.getInstance());
    }
}
