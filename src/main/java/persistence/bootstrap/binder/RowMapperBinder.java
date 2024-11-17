package persistence.bootstrap.binder;

import jdbc.mapper.DefaultRowMapper;
import jdbc.mapper.RowMapper;
import persistence.meta.EntityTable;

import java.util.HashMap;
import java.util.Map;

public class RowMapperBinder {
    private final Map<String, RowMapper> rowMapperRegistry = new HashMap<>();

    public RowMapperBinder(EntityBinder entityBinder, EntityTableBinder entityTableBinder) {
        for (Class<?> entityType : entityBinder.getEntityTypes()) {
            final EntityTable entityTable = entityTableBinder.getEntityTable(entityType);
            final RowMapper defaultRowMapper = createRowMapper(entityTable, entityTableBinder);
            rowMapperRegistry.put(entityType.getTypeName(), defaultRowMapper);
        }
    }

    public RowMapper getRowMapper(Class<?> entityType) {
        return rowMapperRegistry.get(entityType.getName());
    }

    private RowMapper createRowMapper(EntityTable entityTable, EntityTableBinder entityTableBinder) {
        if (entityTable.getAssociationEntityColumn() == null) {
            return new DefaultRowMapper(entityTable);
        }

        final EntityTable childEntityTable = entityTableBinder.getEntityTable(entityTable.getAssociationColumnType());
        return new DefaultRowMapper(entityTable, childEntityTable);
    }
}
