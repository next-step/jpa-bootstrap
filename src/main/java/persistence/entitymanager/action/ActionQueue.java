package persistence.entitymanager.action;

import persistence.entity.context.PersistenceContext;

import java.util.LinkedList;
import java.util.Queue;

/*
요구사항 2 - ActionQueue 를 활용해 쓰기 지연 구현해보기
ActionQueue 어디에서 초기화를 하는 것이 좋을까?
 */
public class ActionQueue {
    private final Queue<InsertAction> insertActions;
    private final Queue<UpdateAction> updateActions;
    private final Queue<DeleteAction> deleteActions;
    private final Queue<SelectAction> selectActions;
    private final PersistenceContext persistenceContext;

    public ActionQueue(PersistenceContext persistenceContext) {
        this.persistenceContext = persistenceContext;

        insertActions = new LinkedList<>();
        updateActions = new LinkedList<>();
        deleteActions = new LinkedList<>();
        selectActions = new LinkedList<>();
    }

    public void addInsertAction(InsertAction action) {
        insertActions.add(action);
    }

    public void addUpdateAction(UpdateAction action) {
        updateActions.add(action);
    }

    public void addDeleteAction(DeleteAction action) {
        deleteActions.add(action);
    }

    public void addSelectAction(SelectAction action) {
        selectActions.add(action);
    }

    public void flush() {
        executeInsert();
        executeUpdate();
        executeDelete();
        executeSelect();
    }

    private void executeInsert() {

    }

    private void executeUpdate() {

    }

    private void executeDelete() {
        for (DeleteAction deleteAction : deleteActions) {
            persistenceContext.removeEntity(deleteAction.getEntity());
        }
    }

    private void executeSelect() {

    }

    public void clear() {
        insertActions.clear();
        updateActions.clear();
        deleteActions.clear();
        selectActions.clear();
    }
}
