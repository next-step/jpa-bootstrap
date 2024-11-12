package persistence.action;

import persistence.entity.EntityEntry;
import persistence.entity.EntityPersister;
import persistence.event.EventSource;

public class EntityInsertAction extends BaseInsertAction{

    private final EventSource source;
    private final Object entity;
    private final EntityPersister entityPersister;
    private final EntityEntry entry;

    public EntityInsertAction(EventSource source,
                              Object entity,
                              EntityPersister entityPersister,
                              EntityEntry entry) {
        this.source = source;
        this.entity = entity;
        this.entityPersister = entityPersister;
        this.entry = entry;
    }

    public boolean isEarlyInsert() {
        return entityPersister.isIdentityIdentifier();
    }

    public Object getEntity() {
        return entity;
    }

    public void execute() {
        entityPersister.insert(entity);
        managePersistedEntity(source, entityPersister, entity, entry);
    }

}
