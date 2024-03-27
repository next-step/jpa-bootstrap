package persistence.entitymanager.event.listeners;

import persistence.entitymanager.action.ActionQueue;
import persistence.entitymanager.action.DeleteAction;
import persistence.entitymanager.event.event.DeleteEvent;

public class DeleteEventListener implements EventListener {
    public void onDelete(DeleteEvent event) {
        ActionQueue actionQueue = event.getSession().getActionQueue();

        actionQueue.addDeleteAction(new DeleteAction(event.getEntity()));
    }
}
