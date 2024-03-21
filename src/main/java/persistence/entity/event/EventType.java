package persistence.entity.event;

import persistence.entity.event.listener.EntityDeleteListener;
import persistence.entity.event.listener.EntityLoadListener;
import persistence.entity.event.listener.EntityMergeListener;
import persistence.entity.event.listener.EntityPersistListener;
import persistence.entity.event.listener.EntityRelationLoadListener;

public enum EventType {
    LOAD(EntityLoadListener.class),
    LOAD_RELATION(EntityRelationLoadListener.class),
    PERSIST(EntityPersistListener.class),
    MERGE(EntityMergeListener.class),
    DELETE(EntityDeleteListener.class),
    ;

    private final Class<?> listenerClass;


    EventType(Class<?> listenerClass) {
        this.listenerClass = listenerClass;
    }

    public Class<?> getListenerClass() {
        return listenerClass;
    }
}
