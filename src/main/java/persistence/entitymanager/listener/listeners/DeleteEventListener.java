package persistence.entitymanager.listener.listeners;

import persistence.entitymanager.actionqueue.ActionQueue;
import persistence.entitymanager.actionqueue.actions.DeleteAction;
import persistence.entitymanager.listener.events.DeleteEvent;

public class DeleteEventListener implements EventListener {
    public void onDelete(DeleteEvent event) {
        ActionQueue actionQueue = event.getSession().getActionQueue();

        actionQueue.addAction(new DeleteAction(event.getEntity()));
    }
}
