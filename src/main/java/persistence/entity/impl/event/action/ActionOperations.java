package persistence.entity.impl.event.action;

import java.util.ArrayList;
import java.util.List;
import persistence.entity.impl.event.EntityAction;

public class ActionOperations {

    private final List<EntityAction> entityActionList;

    private ActionOperations(List<EntityAction> entityActionList) {
        this.entityActionList = entityActionList;
    }

    public static ActionOperations init() {
        return new ActionOperations(new ArrayList<>());
    }

    public void executeAllAction() {
        entityActionList.forEach(EntityAction::execute);
    }

    public void putEntityAction(EntityAction action) {
        this.entityActionList.add(action);
    }
}
