package persistence.sql.dml;

import persistence.meta.EntityColumn;
import persistence.meta.EntityTable;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static persistence.sql.QueryConst.*;

public class SelectQuery {
    public String findAll(EntityTable entityTable) {
        if (entityTable.isOneToMany()) {
            return getAssociationQuery(entityTable)
                    .build();
        }
        return find(entityTable).build();
    }

    public String findById(EntityTable entityTable, Object id) {
        if (entityTable.isOneToMany() && entityTable.isEager()) {
            return getAssociationQuery(entityTable)
                .where(getColumnWithAliasClause(entityTable, entityTable.getIdColumnName()), id)
                    .build();
        }
        return find(entityTable)
                .where(entityTable.getIdColumnName(), id)
                .build();
    }

    public String findCollection(EntityTable entityTable, String columnName, Object id) {
        return find(entityTable)
                .where(columnName, id)
                .build();
    }

    private SelectQueryBuilder getAssociationQuery(EntityTable entityTable) {
        final EntityTable childEntityTable = new EntityTable(entityTable.getAssociationColumnType());
        return new SelectQueryBuilder()
                .select(getSelectClause(entityTable, childEntityTable))
                .from(getTableWithAliasClause(entityTable))
                .innerJoin(getTableWithAliasClause(childEntityTable))
                .on(
                        getColumnWithAliasClause(entityTable, entityTable.getIdColumnName()),
                        getColumnWithAliasClause(childEntityTable, entityTable.getAssociationColumnName())
                );
    }

    private SelectQueryBuilder find(EntityTable entityTable) {
        return new SelectQueryBuilder()
                .select(getSelectClause(entityTable))
                .from(entityTable.getTableName());
    }

    private String getSelectClause(EntityTable entityTable) {
        return entityTable.getEntityColumns()
                .stream()
                .filter(this::isNotNeeded)
                .map(EntityColumn::getColumnName)
                .collect(Collectors.joining(COLUMN_DELIMITER));
    }

    private String getSelectClause(EntityTable entityTable, EntityTable joinEntityTable) {
        final Stream<String> columnDefinitions = entityTable.getEntityColumns()
                .stream()
                .filter(this::isNotNeeded)
                .map(entityColumn -> getJoinColumnName(entityTable, entityColumn));

        final Stream<String> joinColumnDefinitions = joinEntityTable.getEntityColumns()
                .stream()
                .filter(this::isNotNeeded)
                .map(entityColumn -> getJoinColumnName(joinEntityTable, entityColumn));

        return Stream.concat(columnDefinitions, joinColumnDefinitions)
                .collect(Collectors.joining(COLUMN_DELIMITER));
    }

    private boolean isNotNeeded(EntityColumn entityColumn) {
        return !entityColumn.isOneToMany();
    }

    private String getJoinColumnName(EntityTable entityTable, EntityColumn entityColumn) {
        return entityTable.getAlias() + COLUMN_ALIAS_DELIMITER + entityColumn.getColumnName();
    }

    private String getTableWithAliasClause(EntityTable entityTable) {
        return entityTable.getTableName() + TABLE_ALIAS_DELIMITER + entityTable.getAlias();
    }

    private String getColumnWithAliasClause(EntityTable entityTable, String columnName) {
        return entityTable.getAlias() + COLUMN_ALIAS_DELIMITER + columnName;
    }
}