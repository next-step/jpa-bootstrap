package persistence.action;

import java.util.LinkedList;
import java.util.List;

public class ActionQueue {
    private final List<EntityInsertAction> insertions;
    private final List<EntityCollectionInsertAction> collectionInsertions;
    private final List<EntityDeleteAction> deletions;
    private final List<EntityUpdateAction> updates;

    private final List<EntityInsertAction> resolvedInsertions;

    public ActionQueue() {
        insertions = new LinkedList<>();
        collectionInsertions = new LinkedList<>();
        deletions = new LinkedList<>();
        updates = new LinkedList<>();

        resolvedInsertions = new LinkedList<>();
    }

    public void addAction(EntityInsertAction action) {
        if (action.isEarlyInsert()) {
            action.execute();
            resolvedInsertions.add(action);
            return;
        }

        insertions.add(action);
    }

    public void addAction(EntityDeleteAction action) {
        deletions.add(action);
    }

    public void addAction(EntityUpdateAction action) {
        updates.add(action);
    }

    public void addAction(EntityCollectionInsertAction action) {
        for (EntityInsertAction insertion : resolvedInsertions) {
            if (insertion.getEntity().equals(action.getParentEntity())) {
                action.execute();
                return;
            }
        }

        collectionInsertions.add(action);
    }

    public void clear() {
        insertions.clear();
        collectionInsertions.clear();
        deletions.clear();
        updates.clear();
        resolvedInsertions.clear();
    }

    public void executeAll() {
        insertions.forEach(EntityInsertAction::execute);
        collectionInsertions.forEach(EntityCollectionInsertAction::execute);
        updates.forEach(EntityUpdateAction::execute);
        deletions.forEach(EntityDeleteAction::execute);
    }

}
