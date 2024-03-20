package boot.action;

import java.util.LinkedList;
import java.util.Queue;

public class ActionQueue {
    private final Queue<EntityInsertAction<?>> insertions;
    private final Queue<EntityUpdateAction<?>> updates;
    private final Queue<EntityDeleteAction<?>> deletions;

    public ActionQueue(Queue<EntityInsertAction<?>> insertions, Queue<EntityUpdateAction<?>> updates, Queue<EntityDeleteAction<?>> deletions) {
        this.insertions = insertions;
        this.updates = updates;
        this.deletions = deletions;
    }

    public ActionQueue() {
        this(new LinkedList<>(), new LinkedList<>(), new LinkedList<>());
    }

    public void addAction(EntityInsertAction<?> action) {
        insertions.add(action);
    }

    public void addAction(EntityUpdateAction<?> action) {
        updates.add(action);
    }

    public void addAction(EntityDeleteAction action) {
        deletions.add(action);
    }

    public void executeAll() {
        executeAllInsertions();
        executeAllUpdates();
        executeAllDeletes();
    }

    private void executeAllInsertions() {
        while (!insertions.isEmpty()) {
            EntityInsertAction<?> insertAction = insertions.poll();
            insertAction.execute();
        }
    }

    private void executeAllUpdates() {
        while (!updates.isEmpty()) {
            EntityUpdateAction<?> updateAction = updates.poll();
            updateAction.execute();
        }
    }

    private void executeAllDeletes() {
        while (!deletions.isEmpty()) {
            EntityDeleteAction<?> deleteAction = deletions.poll();
            deleteAction.execute();
        }
    }
}
