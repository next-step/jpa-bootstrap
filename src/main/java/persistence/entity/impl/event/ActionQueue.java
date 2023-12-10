package persistence.entity.impl.event;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import persistence.entity.impl.event.action.ActionOperations;
import persistence.entity.impl.event.action.type.ActionType;

public class ActionQueue {

    private final Map<ActionType, ActionOperations> actionOperationMap;

    public ActionQueue() {
        this.actionOperationMap = new LinkedHashMap<>();
        this.actionOperationMap.put(ActionType.INSERT, ActionOperations.init());
        this.actionOperationMap.put(ActionType.UPDATE, ActionOperations.init());
        this.actionOperationMap.put(ActionType.DELETE, ActionOperations.init());
    }

    public void appendAction(EntityAction entityAction) {
        final ActionOperations actionOperations = this.actionOperationMap.get(entityAction.getActionType());
        actionOperations.putEntityAction(entityAction);
    }

    public void executeAllActionOperations() {
        this.actionOperationMap.entrySet().stream()
            .sorted(Comparator.comparing(entry -> entry.getKey().getPriority()))
            .map(Entry::getValue)
            .forEach(ActionOperations::executeAllAction);
    }

    public void clearActionQueue() {
        this.actionOperationMap.values().clear();
    }

    public ActionOperations getActionOperations(ActionType actionType) {
        return this.actionOperationMap.get(actionType);
    }
}
