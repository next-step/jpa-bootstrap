package hibernate.action;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ActionQueue implements InsertUpdateActionQueue, DeleteActionQueue {

    private final Queue<EntityInsertAction<?>> insertions;
    private final Queue<EntityUpdateAction<?>> updates;
    private final Queue<EntityDeleteAction<?>> deletions;

    public ActionQueue(
            final Queue<EntityInsertAction<?>> insertions,
            final Queue<EntityUpdateAction<?>> updates,
            final Queue<EntityDeleteAction<?>> deletions
    ) {
        this.insertions = insertions;
        this.updates = updates;
        this.deletions = deletions;
    }

    public ActionQueue() {
        this(new ConcurrentLinkedQueue<>(), new ConcurrentLinkedQueue<>(), new ConcurrentLinkedQueue<>());
    }

    @Override
    public void addAction(final EntityBasicInsertAction<?> action) {
        if (insertions.contains(action)) {
            return;
        }
        insertions.add(action);
    }

    @Override
    public void addAction(final EntityIdentityInsertAction<?> action) {
        insertions.add(action);
        executeInserts();
    }

    @Override
    public void addAction(final EntityUpdateAction<?> action) {
        updates.add(action);
    }

    @Override
    public void addAction(final EntityDeleteAction<?> action) {
        deletions.add(action);
    }

    public void executeAllActions() {
        executeInserts();
        executeUpdates();
        executeDeletes();
    }

    private void executeInserts() {
        while (!insertions.isEmpty()) {
            insertions.poll()
                    .execute();
        }
    }

    private void executeUpdates() {
        while (!updates.isEmpty()) {
            updates.poll()
                    .execute();
        }
    }

    private void executeDeletes() {
        while (!deletions.isEmpty()) {
            deletions.poll()
                    .execute();
        }
    }
}
