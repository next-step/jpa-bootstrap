package persistence.entity.event.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ActionQueue {
    private final Map<ActionType, List<EntityAction>> actions;
    public ActionQueue() {
        this. actions = Arrays.stream(ActionType.values())
            .filter(actionType -> !actionType.isNoneAction())
            .collect(Collectors.toMap(actionType -> actionType, actionType -> new ArrayList<>()));
    }

    public void addAction(EntityAction action, ActionType actionType) {
        actions.get(actionType).add(action);
    }

    public void executeAll() {
        actions.get(ActionType.INSERT).forEach(EntityAction::execute);
        actions.get(ActionType.UPDATE).forEach(EntityAction::execute);
        actions.get(ActionType.DELETE).forEach(EntityAction::execute);
    }
}
