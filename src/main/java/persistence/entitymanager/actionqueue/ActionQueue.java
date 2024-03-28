package persistence.entitymanager.actionqueue;

import persistence.entity.context.PersistenceContext;
import persistence.entitymanager.actionqueue.actions.DeleteAction;
import persistence.entitymanager.actionqueue.actions.InsertAction;
import persistence.entitymanager.actionqueue.actions.UpdateAction;

import java.util.LinkedList;
import java.util.Queue;

public class ActionQueue {
    private final Queue<InsertAction> insertActions;
    private final Queue<UpdateAction> updateActions;
    private final Queue<DeleteAction> deleteActions;
    private final PersistenceContext persistenceContext;

    public ActionQueue(PersistenceContext persistenceContext) {
        this.persistenceContext = persistenceContext;

        insertActions = new LinkedList<>();
        updateActions = new LinkedList<>();
        deleteActions = new LinkedList<>();
    }

    public void addAction(InsertAction action) {
        insertActions.add(action);
    }

    public void addAction(UpdateAction action) {
        updateActions.add(action);
    }

    public void addAction(DeleteAction action) {
        deleteActions.add(action);
    }

    public void flush() {
        executeInsert();
        executeUpdate();
        executeDelete();

        clear();
    }

    private void executeInsert() {
        for (InsertAction insertAction : insertActions) {
            persistenceContext.insertEntity(insertAction.getEntity());
        }
    }

    private void executeUpdate() {
        for (UpdateAction updateAction : updateActions) {
            persistenceContext.updateEntity(updateAction.getEntity());
        }
    }

    private void executeDelete() {
        for (DeleteAction deleteAction : deleteActions) {
            persistenceContext.removeEntity(deleteAction.getEntity());
        }
    }

    public void clear() {
        insertActions.clear();
        updateActions.clear();
        deleteActions.clear();
    }
}
