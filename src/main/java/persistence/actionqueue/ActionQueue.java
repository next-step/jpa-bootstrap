package persistence.actionqueue;

import java.util.LinkedList;
import java.util.Queue;

public class ActionQueue {
    private final Queue<EntityAction> actions = new LinkedList<>();

    public void addAction(EntityAction action) {

        actions.add(action);
    }

    public void executeActions() {
        while (!actions.isEmpty()) {
            EntityAction action = actions.poll();
            action.execute();
        }
    }
}
