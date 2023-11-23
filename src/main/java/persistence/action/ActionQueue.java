package persistence.action;

public class ActionQueue implements ActionQueueRear, ActionQueueFront {
    private final EntityActionSet<EntityInsertAction> insertions;
    private final EntityActionSet<EntityDeleteAction> deletions;
    private final EntityActionSet<EntityUpdateAction> updates;

    public ActionQueue() {
        this.insertions = new EntityActionSet<>();
        this.deletions = new EntityActionSet<>();
        this.updates = new EntityActionSet<>();
    }

    @Override
    public void addInsertion(final EntityInsertAction entityInsertAction) {
        insertions.add(entityInsertAction);
        // FIXME ID 채번방식에 따른분기
        executeInsert();
    }

    @Override
    public void addDeletion(final EntityDeleteAction entityDeleteAction) {
        deletions.add(entityDeleteAction);
    }

    @Override
    public void addUpdate(final EntityUpdateAction entityUpdateAction) {
        updates.add(entityUpdateAction);
    }

    @Override
    public void executeInsert() {
        insertions.executeAll();
    }

    @Override
    public void executeDelete() {
        deletions.executeAll();
    }

    @Override
    public void executeUpdate() {
        updates.executeAll();
    }

    @Override
    public void flush() {
        executeInsert();
        executeUpdate();
        executeDelete();
    }
}
