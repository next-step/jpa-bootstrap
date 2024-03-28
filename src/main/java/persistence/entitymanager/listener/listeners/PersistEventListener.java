package persistence.entitymanager.listener.listeners;

import persistence.entitymanager.actionqueue.ActionQueue;
import persistence.entitymanager.actionqueue.actions.InsertAction;
import persistence.entitymanager.actionqueue.actions.UpdateAction;
import persistence.entitymanager.listener.events.PersistEvent;

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
