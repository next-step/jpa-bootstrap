package event.impl;

import event.Event;
import persistence.sql.clause.Clause;
import persistence.sql.dml.EntityManager;
import persistence.sql.dml.MetadataLoader;

public record DeleteEvent(Object entity,
                          String entityName,
                          Object entityId,
                          MetadataLoader<?> metadataLoader,
                          EntityManager entityManager) implements Event {

    public static DeleteEvent create(Object entity, EntityManager entityManager) {
        MetadataLoader<?> loader = entityManager.getMetadataLoader(entity.getClass());
        Object primaryKey = Clause.extractValue(loader.getPrimaryKeyField(), entity);

        return new DeleteEvent(entity, loader.getEntityName(), primaryKey, loader, entityManager);
    }
}
