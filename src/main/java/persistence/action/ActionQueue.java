package persistence.action;

import java.util.LinkedHashSet;
import java.util.Set;

public class ActionQueue implements ActionQueueRear, ActionQueueFront {
    private final Set<EntityInsertAction> insertions;
    private final Set<EntityDeleteAction> deletions;
    private final Set<EntityUpdateAction> updates;

    public ActionQueue() {
        this.insertions = new LinkedHashSet<>();
        this.deletions = new LinkedHashSet<>();
        this.updates = new LinkedHashSet<>();
    }

    @Override
    public void addInsertion(final EntityInsertAction entityInsertAction) {
        insertions.removeIf(origin -> origin.equals(entityInsertAction));
        insertions.add(entityInsertAction);
        // FIXME ID 채번방식에 따른분기
        executeInsert();
    }

    @Override
    public void addDeletion(final EntityDeleteAction entityDeleteAction) {
        deletions.removeIf(origin -> origin.equals(entityDeleteAction));
        deletions.add(entityDeleteAction);
    }

    @Override
    public void addUpdate(final EntityUpdateAction entityUpdateAction) {
        updates.removeIf(origin -> origin.equals(entityUpdateAction));
        updates.add(entityUpdateAction);
    }

    @Override
    public void executeInsert() {
        insertions.forEach(EntityInsertAction::execute);
        insertions.clear();
    }

    @Override
    public void executeDelete() {
        deletions.forEach(EntityDeleteAction::execute);
        insertions.clear();
    }

    @Override
    public void executeUpdate() {
        updates.forEach(EntityUpdateAction::execute);
        insertions.clear();
    }

    @Override
    public void flush() {
        executeInsert();
        executeUpdate();
        executeDelete();
    }
}
