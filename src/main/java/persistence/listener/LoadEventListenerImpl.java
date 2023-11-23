package persistence.listener;

import persistence.entity.manager.EntityManager;

public class LoadEventListenerImpl implements LoadEventListener {
    private final EntityManager entityManager;

    public LoadEventListenerImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void onLoad(LoadEvent event) {
        Object entity = entityManager.getPersistenceContext().getEntity(event.getEntityType(), event.getEntityId());
        event.setLoadedEntity(entity);
    }
}
