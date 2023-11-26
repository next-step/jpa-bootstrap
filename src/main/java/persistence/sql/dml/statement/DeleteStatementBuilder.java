package persistence.sql.dml.statement;

import persistence.sql.dml.clause.builder.WhereClauseBuilder;
import persistence.sql.dml.clause.predicate.WherePredicate;
import persistence.sql.exception.EntityMappingException;
import persistence.sql.schema.meta.EntityClassMappingMeta;

public class DeleteStatementBuilder {

    private static final String DELETE_FORMAT = "DELETE FROM %s";
    private static final String DELETE_WHERE_FORMAT = "%s %s";

    private final StringBuilder deleteStatementBuilder;
    private WhereClauseBuilder whereClauseBuilder;

    private DeleteStatementBuilder() {
        this.deleteStatementBuilder = new StringBuilder();
    }

    public static DeleteStatementBuilder builder() {
        return new DeleteStatementBuilder();
    }

    public DeleteStatementBuilder delete(EntityClassMappingMeta entityClassMappingMeta) {

        if (deleteStatementBuilder.length() > 0) {
            throw EntityMappingException.duplicateCallMethod("delete() 메서드");
        }

        deleteStatementBuilder.append(String.format(DELETE_FORMAT, entityClassMappingMeta.tableClause()));
        return this;
    }

    public DeleteStatementBuilder where(WherePredicate predicate) {
        this.whereClauseBuilder = WhereClauseBuilder.builder(predicate);
        return this;
    }

    public DeleteStatementBuilder and(WherePredicate predicate) {
        if (this.whereClauseBuilder == null) {
            throw EntityMappingException.preconditionRequired("where()");
        }

        this.whereClauseBuilder.and(predicate);
        return this;
    }

    public DeleteStatementBuilder or(WherePredicate predicate) {
        if (this.whereClauseBuilder == null) {
            throw EntityMappingException.preconditionRequired("where()");
        }

        this.whereClauseBuilder.or(predicate);
        return this;
    }

    public String build() {
        if (deleteStatementBuilder.length() == 0) {
            throw EntityMappingException.preconditionRequired("delete()");
        }

        if (this.whereClauseBuilder == null) {
            return deleteStatementBuilder.toString();
        }

        return String.format(DELETE_WHERE_FORMAT, deleteStatementBuilder, this.whereClauseBuilder.build());
    }
}
