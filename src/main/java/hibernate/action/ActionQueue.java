package hibernate.action;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ActionQueue {

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

    public void addAction(final EntityBasicInsertAction<?> action) {
        insertions.add(action);
    }

    public void addAction(final EntityIdentityInsertAction<?> action) {
        insertions.add(action);
        executeInserts();
    }

    public void addAction(final EntityUpdateAction<?> action) {
        updates.add(action);
    }

    public void addAction(final EntityDeleteAction<?> action) {
        deletions.add(action);
    }

    private void executeInserts() {
        while (!insertions.isEmpty()) {
            insertions.poll()
                    .execute();
        }
    }
}
