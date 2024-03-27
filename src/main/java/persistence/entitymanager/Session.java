package persistence.entitymanager;

import persistence.entitymanager.action.ActionQueue;

public interface Session {
    ActionQueue getActionQueue();
}
