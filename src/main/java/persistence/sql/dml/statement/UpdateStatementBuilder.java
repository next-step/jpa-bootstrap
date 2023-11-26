package persistence.sql.dml.statement;

import java.util.stream.Collectors;
import persistence.sql.dml.clause.builder.WhereClauseBuilder;
import persistence.sql.dml.clause.operator.EqualOperator;
import persistence.sql.dml.clause.predicate.WherePredicate;
import persistence.sql.exception.EntityMappingException;
import persistence.sql.schema.meta.EntityClassMappingMeta;
import persistence.sql.schema.meta.EntityObjectMappingMeta;

public class UpdateStatementBuilder {

    private static final String UPDATE_FORMAT = "UPDATE %s SET %s";
    private static final String UPDATE_WHERE_FORMAT = "%s %s;";

    private final StringBuilder updateStatementBuilder;
    private WhereClauseBuilder whereClauseBuilder;

    private EntityObjectMappingMeta entityObjectMappingMeta;

    private UpdateStatementBuilder() {
        this.updateStatementBuilder = new StringBuilder();
    }

    public static UpdateStatementBuilder builder() {
        return new UpdateStatementBuilder();
    }

    public UpdateStatementBuilder update(Object entity, EntityClassMappingMeta entityClassMappingMeta) {
        if (updateStatementBuilder.length() > 0) {
            throw EntityMappingException.duplicateCallMethod("update()");
        }

        entityObjectMappingMeta = EntityObjectMappingMeta.of(entity, entityClassMappingMeta);

        updateStatementBuilder.append(
            String.format(UPDATE_FORMAT, entityClassMappingMeta.tableClause(), formatColumnWithUpdatedValue(entityObjectMappingMeta)));
        return this;
    }

    public UpdateStatementBuilder equalById() {
        where(
            WherePredicate.of(entityObjectMappingMeta.getIdColumnName(),
                entityObjectMappingMeta.getIdValue(),
                new EqualOperator())
        );
        return this;
    }

    public UpdateStatementBuilder where(WherePredicate predicate) {
        this.whereClauseBuilder = WhereClauseBuilder.builder(predicate);
        return this;
    }

    public UpdateStatementBuilder and(WherePredicate predicate) {
        if (this.whereClauseBuilder == null) {
            throw EntityMappingException.preconditionRequired("where()");
        }

        this.whereClauseBuilder.and(predicate);
        return this;
    }

    public UpdateStatementBuilder or(WherePredicate predicate) {
        if (this.whereClauseBuilder == null) {
            throw EntityMappingException.preconditionRequired("where()");
        }

        this.whereClauseBuilder.or(predicate);
        return this;
    }

    public String build() {
        if (updateStatementBuilder.length() == 0) {
            throw EntityMappingException.preconditionRequired("update()");
        }

        if (this.whereClauseBuilder == null) {
            return updateStatementBuilder + ";";
        }

        return String.format(UPDATE_WHERE_FORMAT, updateStatementBuilder, this.whereClauseBuilder.build());
    }

    private String formatColumnWithUpdatedValue(EntityObjectMappingMeta objectMappingMeta) {
        return objectMappingMeta.getMetaEntryList().stream()
            .filter(entry -> !entry.getKey().isPrimaryKey())
            .map(entry -> String.format("%s = %s", entry.getKey().getColumnName(), entry.getValue().getFormattedValue()))
            .collect(Collectors.joining(", "));
    }
}
