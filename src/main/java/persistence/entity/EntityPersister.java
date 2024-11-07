package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.definition.ColumnDefinitionAware;
import persistence.sql.definition.TableAssociationDefinition;
import persistence.sql.definition.TableDefinition;
import persistence.sql.dml.query.DeleteQueryBuilder;
import persistence.sql.dml.query.UpdateQueryBuilder;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

public class EntityPersister {
    private static final Long DEFAULT_ID_VALUE = 0L;

    private static final UpdateQueryBuilder updateQueryBuilder = new UpdateQueryBuilder();
    private static final DeleteQueryBuilder deleteQueryBuilder = new DeleteQueryBuilder();

    private final InsertExecutor insertExecutor;
    private final TableDefinition tableDefinition;

    private final JdbcTemplate jdbcTemplate;

    public EntityPersister(TableDefinition tableDefinition, JdbcTemplate jdbcTemplate) {
        this.tableDefinition = tableDefinition;
        this.jdbcTemplate = jdbcTemplate;
        this.insertExecutor = new InsertExecutor(jdbcTemplate, tableDefinition);
    }

    public boolean hasId(Object entity) {
        return tableDefinition.hasId(entity);
    }

    public Serializable getEntityId(Object entity) {
        if (tableDefinition.hasId(entity)) {
            return tableDefinition.getIdValue(entity);
        }

        return DEFAULT_ID_VALUE;
    }

    public Collection<?> getIterableAssociatedValue(Object entity, TableAssociationDefinition association) {
        return tableDefinition.getIterableAssociatedValue(entity, association);
    }

    public Object insert(Object entity) {
        return insertExecutor.insertAndBindKey(entity);
    }

    public List<TableAssociationDefinition> getCollectionAssociations() {
        return tableDefinition.getAssociations().stream().filter(
                TableAssociationDefinition::isCollection
        ).toList();
    }

    public void update(Object entity) {
        final String query = updateQueryBuilder.build(
                getTableName(),
                getIdName(),
                getEntityId(entity),
                getUpdateColumnMaps(entity)
        );
        jdbcTemplate.execute(query);
    }

    private LinkedHashMap<String, Object> getUpdateColumnMaps(Object entity) {
        return getColumns().stream()
                .filter(column -> !column.isPrimaryKey())
                .collect(
                        Collectors.toMap(
                                ColumnDefinitionAware::getDatabaseColumnName,
                                column -> hasValue(entity, column)
                                        ? getQuoted(getValue(entity, column)) : "null",
                                (value1, value2) -> value2,
                                LinkedHashMap::new
                        )
                );
    }

    public void delete(Object entity) {
        String query = deleteQueryBuilder.build(entity, tableDefinition);
        jdbcTemplate.execute(query);
    }

    public String getJoinColumnName(Class<?> entityClass) {
        return tableDefinition.getJoinColumnName(entityClass);
    }

    public Class<?> getEntityClass() {
        return tableDefinition.getEntityClass();
    }

    public Object getColumnValue(Object entity, String joinColumnName) {
        return tableDefinition.getValue(entity, joinColumnName);
    }

    public String getTableName() {
        return tableDefinition.getTableName();
    }

    public Serializable getIdName() {
        return tableDefinition.getIdFieldName();
    }

    public List<? extends ColumnDefinitionAware> getColumns() {
        return tableDefinition.getColumns();
    }

    public boolean hasValue(Object entity, ColumnDefinitionAware column) {
        return tableDefinition.hasValue(entity, column);
    }

    public Object getValue(Object entity, ColumnDefinitionAware column) {
        return tableDefinition.getValue(entity, column);
    }

    private String getQuoted(Object value) {
        if (value instanceof String) {
            return "'" + value + "'";
        }
        return value.toString();
    }

    public List<TableAssociationDefinition> getAssociations() {
        return tableDefinition.getAssociations();
    }
}
