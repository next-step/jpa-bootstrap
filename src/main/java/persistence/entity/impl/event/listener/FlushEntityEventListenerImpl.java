package persistence.entity.impl.event.listener;

import persistence.entity.ContextSource;
import persistence.entity.EventSource;
import persistence.entity.impl.event.EntityEvent;
import persistence.entity.impl.event.EntityEventListener;

public class FlushEntityEventListenerImpl implements EntityEventListener {

    @Override
    public void onEvent(EntityEvent entityEvent) {
        entityEvent.getEventSource().executeAllAction();
    }

    @Override
    public <T> T onEvent(Class<T> clazz, EntityEvent entityEvent) {
        throw new RuntimeException("FlushEventListener는 반환값이 있는 이벤트만 처리할 수 있습니다.");
    }

    @Override
    public void syncContextSource(ContextSource contextSource, Object entity) {
        throw new RuntimeException("ContextSource에 이미 반영되었습니다.");
    }

    @Override
    public void syncEventSource(EventSource eventSource, Object entity) {
        throw new RuntimeException("EventSource에 이미 반영되었습니다.");
    }
}
