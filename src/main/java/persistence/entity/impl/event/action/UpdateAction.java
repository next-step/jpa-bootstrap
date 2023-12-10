package persistence.entity.impl.event.action;

import persistence.entity.impl.event.EntityAction;
import persistence.entity.impl.event.action.type.ActionType;

public class UpdateAction implements EntityAction {

    private final Runnable action;

    public UpdateAction(Runnable action) {
        this.action = action;
    }

    @Override
    public ActionType getActionType() {
        return ActionType.UPDATE;
    }

    @Override
    public void execute() {
        action.run();
    }
}
