package persistence.entity.impl.event;

import persistence.entity.impl.event.action.type.ActionType;

public interface EntityAction {

    ActionType getActionType();

    void execute();
}
