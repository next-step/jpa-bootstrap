package persistence.bootstrap;

import jdbc.JdbcTemplate;
import persistence.entity.EntityPersister;
import persistence.meta.EntityTable;
import persistence.sql.dml.DmlQueries;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityPersisterBinder {
    private final Map<String, EntityPersister> entityPersisterRegistry = new HashMap<>();

    public EntityPersisterBinder(List<Class<?>> entityTypes, EntityTableBinder entityTableBinder,
                                 CollectionLoaderBinder collectionLoaderBinder, JdbcTemplate jdbcTemplate,
                                 DmlQueries dmlQueries) {
        for (Class<?> entityType : entityTypes) {
            final EntityTable entityTable = entityTableBinder.getEntityTable(entityType);
            final EntityPersister entityPersister =
                    new EntityPersister(entityTable, jdbcTemplate, dmlQueries.getInsertQuery(),
                            dmlQueries.getUpdateQuery(), dmlQueries.getDeleteQuery());
            entityPersisterRegistry.put(entityType.getTypeName(), entityPersister);
        }
    }

    public EntityPersister getEntityPersister(Class<?> entityType) {
        return entityPersisterRegistry.get(entityType.getName());
    }
}
