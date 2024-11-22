package event;

import event.impl.ChildEntityInsertAction;
import event.impl.EntityDeleteAction;
import event.impl.EntityInsertAction;
import event.impl.EntityUpdateAction;

import java.util.ArrayDeque;
import java.util.Queue;

public class ActionQueue {
    private final Queue<EntityInsertAction<?>> insertions = new ArrayDeque<>();
    private final Queue<EntityDeleteAction<?>> deletions = new ArrayDeque<>();
    private final Queue<EntityUpdateAction<?>> updates = new ArrayDeque<>();
    private final Queue<ChildEntityInsertAction<?, ?>> childEntityInsertions = new ArrayDeque<>();

    public void executeAction() {
        executeAction(insertions);
        executeAction(childEntityInsertions);
        executeAction(updates);
        executeAction(deletions);
    }

    private void executeAction(Queue<? extends EntityAction> actions) {
        while (!actions.isEmpty()) {
            actions.poll().execute();
        }
    }

    public void addInsertion(EntityInsertAction<?> action) {
        insertions.add(action);
    }

    public void addChildEntityInsertion(ChildEntityInsertAction<?, ?> action) {
        childEntityInsertions.add(action);
    }

    public void addDeletion(EntityDeleteAction<?> action) {
        deletions.add(action);
    }

    public void addUpdate(EntityUpdateAction<?> action) {
        updates.add(action);
    }
}
