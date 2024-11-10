package persistence.meta;

public class AssociationCondition {
    private final String columnName;
    private final Object id;

    public AssociationCondition(String columnName, Object id) {
        this.columnName = columnName;
        this.id = id;
    }

    public String getColumnName() {
        return columnName;
    }

    public Object getId() {
        return id;
    }
}
