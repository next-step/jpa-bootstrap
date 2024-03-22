package boot.action;

import java.util.LinkedHashSet;
import java.util.Set;

public class ActionQueue {
    private final Set<EntityInsertAction<?>> insertions;
    private final Set<EntityUpdateAction<?>> updates;
    private final Set<EntityDeleteAction<?>> deletions;

    public ActionQueue(Set<EntityInsertAction<?>> insertions, Set<EntityUpdateAction<?>> updates,
                       Set<EntityDeleteAction<?>> deletions) {
        this.insertions = insertions;
        this.updates = updates;
        this.deletions = deletions;
    }

    public ActionQueue() {
        this(new LinkedHashSet<>(), new LinkedHashSet<>(), new LinkedHashSet<>());
    }

    public void addAction(EntityInsertAction<?> action) {
        insertions.add(action);
        executeAllInsertions();
    }

    public void addAction(EntityUpdateAction<?> action) {
        updates.remove(action);
        updates.add(action);
    }

    public void addAction(EntityDeleteAction<?> action) {
        deletions.remove(action);
        deletions.add(action);
    }

    public void executeAll() {
        executeAllInsertions();
        executeAllUpdates();
        executeAllDeletes();
    }

    private void executeAllInsertions() {
        insertions.forEach(EntityInsertAction::execute);
        insertions.clear();
    }

    private void executeAllUpdates() {
        updates.forEach(EntityUpdateAction::execute);
        updates.clear();
    }

    private void executeAllDeletes() {
        deletions.forEach(EntityDeleteAction::execute);
        deletions.clear();
    }
}
