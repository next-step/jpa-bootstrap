package persistence.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.entity.EntityKey;
import persistence.entity.EntityPersister;
import persistence.event.EventSource;

public class EntityDeleteAction {

    private static final Logger logger = LoggerFactory.getLogger(EntityDeleteAction.class);

    private final EventSource source;
    private final Object entity;
    private final EntityPersister entityPersister;

    public EntityDeleteAction(EventSource source,
                              Object entity,
                              EntityPersister entityPersister) {

        this.source = source;
        this.entity = entity;
        this.entityPersister = entityPersister;
    }

    public void execute() {
        entityPersister.delete(entity);

        source.getPersistenceContext().removeEntity(
                new EntityKey(entityPersister.getEntityId(entity), entity.getClass())
        );

        logger.info("""
                Entity with id {} and class {} has been deleted.
                """, entityPersister.getEntityId(entity), entity.getClass().getName());
    }
}
