package event.action;

import java.util.LinkedList;
import java.util.Queue;

public class ActionQueue {
    private final Queue<EntityPersistAction> entityPersistActions = new LinkedList<>();
    private final Queue<EntityMergeAction> entityMergeActions = new LinkedList<>();
    private final Queue<EntityDeleteAction> entityDeleteActions = new LinkedList<>();

    public void addAction(EntityPersistAction entityPersistAction) {
        entityPersistActions.add(entityPersistAction);
    }

    public void addAction(EntityMergeAction entityMergeAction) {
        entityMergeActions.add(entityMergeAction);
    }

    public void addAction(EntityDeleteAction entityDeleteAction) {
        entityDeleteActions.add(entityDeleteAction);
    }

    public void execute() {
        persistActionExecute();
        mergeActionExecute();
        deleteActionExecute();
    }

    private void persistActionExecute() {
        while (!entityPersistActions.isEmpty()) {
            entityPersistActions.poll().execute();
        }
    }

    private void mergeActionExecute() {
        while (!entityMergeActions.isEmpty()) {
            entityMergeActions.poll().execute();
        }
    }

    private void deleteActionExecute() {
        while (!entityDeleteActions.isEmpty()) {
            entityDeleteActions.poll().execute();
        }
    }
}
