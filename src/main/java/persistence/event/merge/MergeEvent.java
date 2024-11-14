package persistence.event.merge;

import persistence.entity.EntityEntry;
import persistence.event.AbstractEvent;
import persistence.event.EventSource;

public class MergeEvent extends AbstractEvent {

    private final Object entity;
    private final EntityEntry entry;

    private MergeEvent(EventSource source, Object entity, EntityEntry entry) {
        super(source);
        this.entity = entity;
        this.entry = entry;
    }

    public static MergeEvent create(EventSource source, Object entity, EntityEntry entry) {
        return new MergeEvent(source, entity, entry);
    }

    public Object getEntity() {
        return entity;
    }

    public EntityEntry getEntry() {
        return entry;
    }
}
