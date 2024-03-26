package database.sql.dml;

import database.mapping.Association;
import database.mapping.column.EntityColumn;
import database.sql.dml.part.WhereClause;
import database.sql.dml.part.WhereMap;
import persistence.bootstrap.Metadata;
import persistence.entity.context.PersistentClass;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CustomSelect {
    private static final String TABLE_ALIAS = "t";
    private static final String ASSOCIATED_TABLE_ALIAS_PREFIX = "a";
    private static final String SELECT = "SELECT %s FROM %s";
    private static final String QUERY_WITH_WHERE = "%s WHERE %s";
    private static final String LEFT_JOIN_CLAUSE = "LEFT JOIN %s ON %s = %s";
    private static final String COLUMNS_DELIMITER = ", ";

    private final String tableName;
    private final List<EntityColumn> allEntityColumns;
    private final List<Association> associations;
    private final List<String> allColumnNamesWithAssociations;
    private final Metadata metadata;

    public static <T> CustomSelect from(PersistentClass<T> persistentClass, Metadata metadata) {
        return new CustomSelect(
                persistentClass.getTableName(),
                persistentClass.getAllEntityColumns(),
                persistentClass.getAssociations(),
                metadata.getAllColumnNamesWithAssociations(persistentClass),
                metadata
        );
    }

    private CustomSelect(String tableName, List<EntityColumn> allEntityColumns,
                         List<Association> associations, List<String> allColumnNamesWithAssociations,
                         Metadata metadata) {
        this.tableName = tableName;
        this.allEntityColumns = allEntityColumns;
        this.associations = associations;
        this.allColumnNamesWithAssociations = allColumnNamesWithAssociations;
        this.metadata = metadata;
    }

    public String toSql() {
        String columns = String.join(COLUMNS_DELIMITER, selectColumns());
        String query = String.format(SELECT, columns, tableWithAlias(this.tableName, TABLE_ALIAS));
        if (!joins().isEmpty()) {
            query = query + " " + String.join(" ", joins());
        }
        return query;
    }

    public String toSql(WhereMap whereMap) {
        return String.format(QUERY_WITH_WHERE, toSql(), whereClause(whereMap));
    }

    private List<String> selectColumns() {
        List<String> columns = new LinkedList<>(primaryTableColumns());
        for (int tableIndex = 0; tableIndex < associations.size(); tableIndex++) {
            if (associations.get(tableIndex).isLazyLoad()) {
                continue;
            }
            columns.addAll(associatedTableColumns(tableIndex));
        }
        return columns;
    }

    private List<String> primaryTableColumns() {
        return allEntityColumns.stream()
                .map(entityColumn -> columnWithAlias(entityColumn.getColumnName(), TABLE_ALIAS))
                .collect(Collectors.toList());
    }

    private List<String> associatedTableColumns(int index) {
        Association association = associations.get(index);
        String tableAlias = associatedTableAliasOf(index);
        List<EntityColumn> allEntityColumns = association.getGenericTypeClass(metadata).getAllEntityColumns();

        String foreignKeyColumnName = association.getForeignKeyColumnName();

        List<String> columns = new ArrayList<>();
        columns.add(columnWithAlias(foreignKeyColumnName, tableAlias));
        for (EntityColumn allEntityColumn : allEntityColumns) {
            String columnName = allEntityColumn.getColumnName();
            columns.add(columnWithAlias(columnName, tableAlias));
        }
        return columns;
    }

    private List<String> joins() {
        return IntStream.range(0, associations.size())
                .filter(index -> !associations.get(index).isLazyLoad())
                .mapToObj(this::eachJoin)
                .collect(Collectors.toList());
    }

    private String eachJoin(int index) {
        Association association = associations.get(index);

        String tableName = association.getTableName(metadata);
        String foreignKeyColumnName = association.getForeignKeyColumnName();
        String tableAlias = associatedTableAliasOf(index);

        return String.format(LEFT_JOIN_CLAUSE,
                             tableWithAlias(tableName, tableAlias),
                             columnWithAlias("id", TABLE_ALIAS),
                             columnWithAlias(foreignKeyColumnName, tableAlias));
    }

    private static String columnWithAlias(String columnName, String alias) {
        return alias + "." + columnName;
    }

    private static String tableWithAlias(String tableName, String alias) {
        return tableName + " " + alias;
    }

    private static String associatedTableAliasOf(int index) {
        return ASSOCIATED_TABLE_ALIAS_PREFIX + index;
    }

    private String whereClause(WhereMap whereMap) {
        return WhereClause.from(whereMap, allColumnNamesWithAssociations, TABLE_ALIAS)
                .withWhereClause(false)
                .toQuery();
    }
}
