package persistence.entity.impl.event.action;

import persistence.entity.impl.event.EntityAction;
import persistence.entity.impl.event.action.type.ActionType;

public class DeleteAction implements EntityAction {

    private final Runnable action;

    public DeleteAction(Runnable action) {
        this.action = action;
    }

    @Override
    public ActionType getActionType() {
        return ActionType.DELETE;
    }

    @Override
    public void execute() {
        action.run();
    }
}
