package persistence.action;

public class ActionQueue {
    private final EntityActionSet<EntityInsertAction> insertions;
    private final EntityActionSet<EntityDeleteAction> deletions;
    private final EntityActionSet<EntityUpdateAction> updates;

    public ActionQueue() {
        this.insertions = new EntityActionSet<>();
        this.deletions = new EntityActionSet<>();
        this.updates = new EntityActionSet<>();
    }

    public void addInsertion(final EntityInsertAction entityInsertAction) {
        insertions.add(entityInsertAction);
        // FIXME ID 채번방식에 따른분기
        executeInsert();
    }

    public void addDeletion(final EntityDeleteAction entityDeleteAction) {
        deletions.add(entityDeleteAction);
    }

    public void addUpdate(final EntityUpdateAction entityUpdateAction) {
        updates.add(entityUpdateAction);
    }

    public void executeInsert() {
        insertions.executeAll();
    }

    public void executeDelete() {
        deletions.executeAll();
    }

    public void executeUpdate() {
        updates.executeAll();
    }

    public void flush() {
        executeInsert();
        executeUpdate();
        executeDelete();
    }
}
