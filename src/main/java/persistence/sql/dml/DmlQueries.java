package persistence.sql.dml;

public class DmlQueries {
    private final SelectQuery selectQuery;
    private final InsertQuery insertQuery;
    private final UpdateQuery updateQuery;
    private final DeleteQuery deleteQuery;

    public DmlQueries() {
        this.selectQuery = new SelectQuery();
        this.insertQuery = new InsertQuery();
        this.updateQuery = new UpdateQuery();
        this.deleteQuery = new DeleteQuery();
    }

    public SelectQuery getSelectQuery() {
        return selectQuery;
    }

    public InsertQuery getInsertQuery() {
        return insertQuery;
    }

    public UpdateQuery getUpdateQuery() {
        return updateQuery;
    }

    public DeleteQuery getDeleteQuery() {
        return deleteQuery;
    }
}
