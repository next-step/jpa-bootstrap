package persistence.entity;

import persistence.entity.impl.event.EntityAction;

public interface EventSource {

    void addAction(EntityAction entityAction);

    void executeAllAction();
}
