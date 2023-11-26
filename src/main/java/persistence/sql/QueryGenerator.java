package persistence.sql;

import persistence.dialect.Dialect;
import persistence.sql.ddl.CreateQueryBuilder;
import persistence.sql.ddl.DropQueryBuilder;
import persistence.sql.dml.DeleteQueryBuilder;
import persistence.sql.dml.InsertQueryBuilder;
import persistence.sql.dml.SelectQueryBuilder;
import persistence.sql.dml.UpdateQueryBuilder;

public class QueryGenerator {
    private final Dialect dialect;

    private QueryGenerator(Dialect dialect) {
        if (dialect == null) {
            throw new IllegalArgumentException("dialect 는 필수 입니다.");
        }
        this.dialect = dialect;
    }

    public static QueryGenerator of(Dialect dialect) {
        return new QueryGenerator(dialect);
    }

    public CreateQueryBuilder create() {
        return new CreateQueryBuilder(dialect);
    }

    public DropQueryBuilder drop() {
        return new DropQueryBuilder(dialect);
    }

    public InsertQueryBuilder insert() {
        return new InsertQueryBuilder(dialect);
    }

    public DeleteQueryBuilder delete() {
        return new DeleteQueryBuilder(dialect);
    }

    public SelectQueryBuilder select() {
        return new SelectQueryBuilder(dialect);
    }

    public UpdateQueryBuilder update() {
        return new UpdateQueryBuilder(dialect);
    }


}
