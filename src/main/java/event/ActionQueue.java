package event;

import event.impl.EntityDeleteAction;
import event.impl.EntityInsertAction;
import event.impl.EntityUpdateAction;

import java.util.ArrayList;
import java.util.List;

public class ActionQueue {
    private final List<EntityInsertAction> insertions = new ArrayList<>();
    private final List<EntityDeleteAction> deletions = new ArrayList<>();
    private final List<EntityUpdateAction> updates = new ArrayList<>();

    public void addInsertion(EntityInsertAction action) {
        insertions.add(action);
    }

    public void addDeletion(EntityDeleteAction action) {
        deletions.add(action);
    }

    public void addUpdate(EntityUpdateAction action) {
        updates.add(action);
    }


}
