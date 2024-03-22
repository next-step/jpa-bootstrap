package persistence.entity.event;

import persistence.entity.event.action.ActionType;
import persistence.entity.event.listener.EntityDeleteListener;
import persistence.entity.event.listener.EntityLoadListener;
import persistence.entity.event.listener.EntityMergeListener;
import persistence.entity.event.listener.EntityPersistListener;
import persistence.entity.event.listener.EntityRelationLoadListener;

public enum EventType {
    LOAD(ActionType.NONE, EntityLoadListener.class),
    LOAD_RELATION(ActionType.NONE, EntityRelationLoadListener.class),
    PERSIST(ActionType.INSERT, EntityPersistListener.class),
    MERGE(ActionType.UPDATE, EntityMergeListener.class),
    DELETE(ActionType.DELETE, EntityDeleteListener.class),
    ;

    private final ActionType actionType;
    private final Class<?> listenerClass;


    EventType(ActionType actionType, Class<?> listenerClass) {
        this.actionType = actionType;
        this.listenerClass = listenerClass;
    }

    public Class<?> getListenerClass() {
        return listenerClass;
    }

    public ActionType getActionType() {
        return actionType;
    }
}
