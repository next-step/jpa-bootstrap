package persistence.action;

public interface ActionQueueRear {
    void addInsertion(EntityInsertAction entityInsertAction);
    void addDeletion(EntityDeleteAction entityInsertAction);
    void addUpdate(EntityUpdateAction entityInsertAction);
}
