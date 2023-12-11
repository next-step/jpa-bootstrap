package persistence.entity.impl.event.action;

import persistence.entity.impl.event.EntityAction;
import persistence.entity.impl.event.action.type.ActionType;

public class InsertAction implements EntityAction {

    private final Runnable action;

    public InsertAction(Runnable action) {
        this.action = action;
    }

    @Override
    public ActionType getActionType() {
        return ActionType.INSERT;
    }

    @Override
    public void execute() {
        action.run();
    }
}
