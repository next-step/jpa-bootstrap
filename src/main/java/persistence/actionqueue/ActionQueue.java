package persistence.actionqueue;

import java.util.ArrayList;
import java.util.List;

public class ActionQueue {
    private final List<EntityAction> actions = new ArrayList<>();

    public void addAction(EntityAction action) {
        actions.add(action);
    }

    public void executeActions() {
        for (EntityAction action : actions) {
            action.execute();
        }
    }
}
