package persistence.sql.dml;

import persistence.meta.EntityColumn;
import persistence.meta.EntityTable;

import java.util.List;
import java.util.stream.Collectors;

public class InsertQuery {
    public String insert(Object entity) {
        final EntityTable entityTable = new EntityTable(entity);
        return new InsertQueryBuilder()
                .insertInto(entityTable.getTableName(), getColumns(entityTable))
                .values(getValues(entityTable))
                .build();
    }

    @SuppressWarnings("rawtypes")
    public String insert(Object entity, Object parentEntity) {
        final EntityTable entityTable = new EntityTable(entity);
        final EntityTable parentEntityTable = new EntityTable(parentEntity);
        final EntityColumn joinEntityColumn = parentEntityTable.getAssociationEntityColumn();
        final List joinEntities = (List) joinEntityColumn.getValue();

        if (joinEntities.contains(entity)) {
            return new InsertQueryBuilder()
                    .insertInto(entityTable.getTableName(), getColumns(entityTable, parentEntityTable))
                    .values(getValues(entityTable, parentEntityTable))
                    .build();
        }
        return new InsertQueryBuilder()
                .insertInto(entityTable.getTableName(), getColumns(entityTable))
                .values(getValues(entityTable))
                .build();
    }

    private List<String> getColumns(EntityTable entityTable, EntityTable parentEntityTable) {
        final List<String> columnClause = getColumns(entityTable);
        columnClause.add(parentEntityTable.getAssociationColumnName());
        return columnClause;
    }

    private List<String> getColumns(EntityTable entityTable) {
        return entityTable.getEntityColumns()
                .stream()
                .filter(this::isAvailable)
                .map(EntityColumn::getColumnName)
                .collect(Collectors.toList());
    }

    private List<Object> getValues(EntityTable entityTable, EntityTable parentEntityTable) {
        final List<Object> valueClause = getValues(entityTable);
        valueClause.add(parentEntityTable.getIdValue());
        return valueClause;
    }

    private List<Object> getValues(EntityTable entityTable) {
        return entityTable.getEntityColumns()
                .stream()
                .filter(this::isAvailable)
                .map(EntityColumn::getValue)
                .collect(Collectors.toList());
    }

    private boolean isAvailable(EntityColumn entityColumn) {
        return !entityColumn.isGenerationValue() && !entityColumn.isOneToMany();
    }
}
