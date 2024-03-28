package persistence.entitymanager;

import persistence.entity.context.PersistenceContext;
import persistence.entitymanager.action.ActionQueue;

public interface Session {
    ActionQueue getActionQueue();

    PersistenceContext getPersistenceContext();
}
