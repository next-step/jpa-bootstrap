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
    private final EntityPersister entityPersister;
    private final List<String> columns = new ArrayList<>();
    private final Map<String, String> conditions = new HashMap<>();

    private EntityPersister joinEntityPersister;
    private final List<String> joinTableColumns = new ArrayList<>();

    public SelectQueryBuilder(Class<?> entityClass, Metamodel metamodel) {
        final EntityPersister entityPersister = metamodel.findEntityPersister(entityClass);
        this.entityPersister = entityPersister;
        entityPersister.getColumns().forEach(column -> {
                    columns.add(column.getDatabaseColumnName());
                }
        );
    }

    public void join(EntityPersister joinEntityPersister) {
        this.joinEntityPersister = joinEntityPersister;
        this.joinEntityPersister.getColumns().forEach(column -> {
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
                .append(entityPersister.getTableName());
    }

    private String columnsClause() {
        final StringJoiner joiner = new StringJoiner(", ");

        columns.forEach(column -> {
            final String aliased = AliasRule.with(entityPersister.getTableName(), column);
            joiner.add(entityPersister.getTableName() + "." + column + " AS " + aliased);
        });

        joinTableColumns.forEach(column -> {
            final String aliased = AliasRule.with(joinEntityPersister.getTableName(), column);
            joiner.add(joinEntityPersister.getTableName() + "." + column + " AS " + aliased);
        });

        return joiner.toString();
    }

    private void joinClause() {
        if (joinEntityPersister != null) {
            query.append(" LEFT JOIN ")
                    .append(joinEntityPersister.getTableName())
                    .append(" ON ")
                    .append(joinEntityPersister.getTableName())
                    .append(".")
                    .append(entityPersister.getJoinColumnName(joinEntityPersister.getEntityClass()))
                    .append(" = ")
                    .append(entityPersister.getTableName())
                    .append(".")
                    .append(entityPersister.getIdColumnName());
        }
    }

    private void whereClause() {
        if (conditions.isEmpty()) {
            return;
        }
        final StringJoiner joiner = new StringJoiner(" AND ");

        query.append(" WHERE ");
        conditions.forEach((column, value) -> {
            joiner.add(entityPersister.getTableName() + "." + column + " = " + getQuoted(value));
        });
        query.append(joiner);
    }

    private void whereByIdClause(Serializable id) {
        query
                .append(" WHERE ")
                .append(entityPersister.getTableName())
                .append(".")
                .append(entityPersister.getIdColumnName())
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
