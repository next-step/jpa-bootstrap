package persistence.entity.event.action;


import java.util.LinkedList;
import java.util.Queue;

public class ActionQueue {

    private final Queue<EntityInsertAction> insertActionQueue;
    private final Queue<EntityUpdateAction> updateActionQueue;
    private final Queue<EntityDeleteAction> deleteActionQueue;


    public ActionQueue() {
        this.insertActionQueue = new LinkedList<>();
        this.updateActionQueue = new LinkedList<>();
        this.deleteActionQueue = new LinkedList<>();
    }

    public <T, ID> void addAction(EntityDeleteAction<T, ID> action) {
        if (!deleteActionQueue.contains(action)) {
            deleteActionQueue.add(action);
        }
    }

    public <T> void addAction(EntityInsertAction<T> action) {
        insertActionQueue.add(action);
    }

    public <T, ID> void addAction(EntityUpdateAction<T, ID> action) {
        if (!updateActionQueue.contains(action)) {
            updateActionQueue.add(action);
        }
    }

    public void executeAllActions() {
        executeInsertActions();
        executeUpdateActions();
        executeDeleteActions();
    }

    private void executeInsertActions() {
        while (!insertActionQueue.isEmpty()) {
            insertActionQueue.poll().execute();
        }
    }

    private void executeUpdateActions() {
        while (!updateActionQueue.isEmpty()) {
            updateActionQueue.poll().execute();
        }
    }

    private void executeDeleteActions() {
        while (!deleteActionQueue.isEmpty()) {
            deleteActionQueue.poll().execute();
        }
    }
}
