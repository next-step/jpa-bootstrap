package persistence.event;

import persistence.action.ActionQueue;
import persistence.entity.EntityPersister;
import persistence.entity.PersistenceContext;
import persistence.session.EntityManager;

public interface EventSource extends EntityManager {

    ActionQueue getActionQueue();

    PersistenceContext getPersistenceContext();

    EntityPersister findEntityPersister(Class<?> clazz);

}
