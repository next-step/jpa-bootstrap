package persistence.event.delete;

import persistence.entity.EntityEntry;
import persistence.event.AbstractEvent;
import persistence.event.EventSource;

public class DeleteEvent extends AbstractEvent {
    private final Object entity;
    private final EntityEntry entry;

    private DeleteEvent(EventSource source, Object entity, EntityEntry entry) {
        super(source);
        this.entity = entity;
        this.entry = entry;
    }

    public static DeleteEvent create(EventSource source, Object entity, EntityEntry entry) {
        return new DeleteEvent(source, entity, entry);
    }

    public Object getEntity() {
        return entity;
    }

    public EntityEntry getEntry() {
        return entry;
    }
}
