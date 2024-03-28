package persistence.entitymanager.event.listeners;

import persistence.entitymanager.action.ActionQueue;
import persistence.entitymanager.action.InsertAction;
import persistence.entitymanager.action.UpdateAction;
import persistence.entitymanager.event.event.PersistEvent;

public class PersistEventListener {
    public void onPersist(PersistEvent event) {
        ActionQueue actionQueue = event.getSession().getActionQueue();

        if (event.hasNewEntity()) {
            InsertAction insertAction = new InsertAction(event.getEntity());
            actionQueue.addAction(insertAction);
        } else {
            UpdateAction updateAction = new UpdateAction(event.getEntity());
            actionQueue.addAction(updateAction);
        }
    }
}
