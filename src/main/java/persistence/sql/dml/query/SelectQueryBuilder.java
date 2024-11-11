package persistence.sql.dml.query;

import common.AliasRule;
import common.SqlLogger;
import persistence.entity.EntityPersister;
import persistence.meta.Metamodel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class SelectQueryBuilder implements BaseQueryBuilder {
    private final StringBuilder query = new StringBuilder();
    private final String tableName;
    private final String idColumnName;
    private final List<String> columns = new ArrayList<>();
    private final Map<String, String> conditions = new HashMap<>();

    private String joinTableName;
    private String joinColumnName;
    private final List<String> joinTableColumns = new ArrayList<>();

    public SelectQueryBuilder(Class<?> entityClass, Metamodel metamodel) {
        final EntityPersister entityPersister = metamodel.findEntityPersister(entityClass);
        this.tableName = entityPersister.getTableName();
        this.idColumnName = entityPersister.getIdColumnName();
        entityPersister.getColumns().forEach(column -> {
                    columns.add(column.getDatabaseColumnName());
                }
        );
    }

    public void join(String joinColumnName,
                     EntityPersister joinEntityPersister) {

        this.joinTableName = joinEntityPersister.getTableName();
        this.joinColumnName = joinColumnName;
        joinEntityPersister.getColumns().forEach(column -> {
                    joinTableColumns.add(column.getDatabaseColumnName());
                }
        );
    }

    public SelectQueryBuilder where(String column, String value) {
        conditions.put(column, value);
        return this;
    }

    private void selectClause() {
        query.append("SELECT ")
                .append(columnsClause())
                .append(" FROM ")
                .append(tableName);
    }

    private String columnsClause() {
        final StringJoiner joiner = new StringJoiner(", ");

        columns.forEach(column -> {
            final String aliased = AliasRule.with(tableName, column);
            joiner.add(tableName + "." + column + " AS " + aliased);
        });

        joinTableColumns.forEach(column -> {
            final String aliased = AliasRule.with(joinTableName, column);
            joiner.add(joinTableName + "." + column + " AS " + aliased);
        });

        return joiner.toString();
    }

    private void joinClause() {
        if (joinTableName != null) {
            query.append(" LEFT JOIN ")
                    .append(joinTableName)
                    .append(" ON ")
                    .append(joinTableName)
                    .append(".")
                    .append(joinColumnName)
                    .append(" = ")
                    .append(tableName)
                    .append(".")
                    .append(idColumnName);
        }
    }

    private void whereClause() {
        if (conditions.isEmpty()) {
            return;
        }
        final StringJoiner joiner = new StringJoiner(" AND ");

        query.append(" WHERE ");
        conditions.forEach((column, value) -> {
            joiner.add(tableName + "." + column + " = " + getQuoted(value));
        });
        query.append(joiner);
    }

    private void whereByIdClause(Serializable id) {
        query
                .append(" WHERE ")
                .append(tableName)
                .append(".")
                .append(idColumnName)
                .append(" = ")
                .append(getQuoted(id)).append(";");
    }

    public String buildById(Serializable id) {
        selectClause();
        joinClause();
        whereByIdClause(id);

        final String sql = query.toString();
        SqlLogger.infoSelect(sql);
        return sql;
    }

    public String build() {
        selectClause();
        whereClause();

        final String sql = query.toString();
        SqlLogger.infoSelect(sql);
        return sql;
    }

}
