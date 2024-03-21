package persistence.entity.event.listener;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import persistence.entity.event.EntityEvent;
import persistence.entity.event.EventType;
import persistence.sql.meta.MetaModel;

public class EntityEventDispatcher {

    private final Map<EventType, EntityListener> listenerMap;

    public EntityEventDispatcher(MetaModel metaModel) {
        this.listenerMap = Arrays.stream(EventType.values())
            .collect(Collectors.toMap(
                eventType -> eventType,
                eventType -> createListener(eventType, metaModel)
            ));
    }

    private EntityListener createListener(EventType eventType, MetaModel metaModel) {
        try {
            return (EntityListener) eventType.getListenerClass().getConstructor(MetaModel.class).newInstance(metaModel);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <T> Object dispatch(EntityEvent<T> event, EventType eventType) {
        EntityListener listener = listenerMap.get(eventType);
        if (listener != null) {
            return listener.handleEvent(event);
        }
        throw new IllegalArgumentException("존재하지 않은 이벤트 리스너 입니다.");
    }
}
