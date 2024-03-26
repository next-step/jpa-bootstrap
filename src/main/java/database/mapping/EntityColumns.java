package database.mapping;

import database.mapping.column.GeneralEntityColumn;
import database.mapping.column.PrimaryKeyEntityColumn;
import persistence.entity.context.PersistentClass;

import java.util.List;
import java.util.stream.Collectors;

public class EntityColumns {
    PrimaryKeyEntityColumn primaryKeyName;
    List<GeneralEntityColumn> generalColumns;
    List<Association> associationColumns;

    public static EntityColumns columns(PersistentClass<?> persistentClass, List<Class<?>> entityClasses) {
        EntityColumns columns = new EntityColumns(
                persistentClass.getPrimaryKey(),
                persistentClass.getGeneralColumns(),
                persistentClass.getAssociationsRelatedTo(entityClasses));
        columns.clearDuplications();
        return columns;
    }

    private EntityColumns(
            PrimaryKeyEntityColumn primaryKeyName,
            List<GeneralEntityColumn> generalColumns,
            List<Association> associationColumns) {
        this.primaryKeyName = primaryKeyName;
        this.generalColumns = generalColumns;
        this.associationColumns = associationColumns;
    }

    /**
     * generalColumns 안에 있는 것에서 associationColumns 와 겹치는 것을 제외한다.
     */
    private void clearDuplications() {
        List<String> associationColumnNames = associationColumns.stream()
                .map(Association::getForeignKeyColumnName).collect(Collectors.toList());
        generalColumns = generalColumns.stream()
                .filter(it -> !associationColumnNames.contains(it.getColumnName()))
                .collect(Collectors.toList());
    }

    public PrimaryKeyEntityColumn getPrimaryKey() {
        return primaryKeyName;
    }

    public List<GeneralEntityColumn> getGeneralColumns() {
        return generalColumns;
    }

    public List<Association> getAssociationColumns() {
        return associationColumns;
    }
}
