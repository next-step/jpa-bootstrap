package database.sql.dml;

import database.mapping.column.PrimaryKeyEntityColumn;
import database.sql.dml.part.WhereClause;
import database.sql.dml.part.WhereMap;
import persistence.bootstrap.Metadata;
import persistence.entity.context.PersistentClass;

import java.util.List;

public class Delete {
    private final String tableName;
    private final PrimaryKeyEntityColumn primaryKey;
    private final List<String> allColumnNamesWithAssociations;

    public static <T> Delete from(PersistentClass<T> persistentClass, Metadata metadata) {
        return new Delete(
                persistentClass.getTableName(),
                persistentClass.getPrimaryKey(),
                metadata.getAllColumnNamesWithAssociations(persistentClass)
        );
    }

    private Delete(String tableName, PrimaryKeyEntityColumn primaryKey, List<String> allColumnNamesWithAssociations) {
        this.tableName = tableName;
        this.primaryKey = primaryKey;
        this.allColumnNamesWithAssociations = allColumnNamesWithAssociations;
    }

    public String toSql(Long id) {
        WhereMap where = WhereMap.of(primaryKey.getColumnName(), id);
        return toSql(where);
    }

    public String toSql(WhereMap where) {
        return String.format("DELETE FROM %s %s",
                             tableName,
                             WhereClause.from(where, allColumnNamesWithAssociations).toQuery());
    }
}
