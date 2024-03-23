package database.sql.dml;

import database.sql.dml.part.WhereClause;
import database.sql.dml.part.WhereMap;
import persistence.bootstrap.Metadata;
import persistence.entity.context.PersistentClass;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class Select {
    private static final String COLUMNS_DELIMITER = ", ";

    private final String tableName;
    private final String primaryKeyColumnName;
    private final List<String> generalEntityColumnNames;
    private final List<String> allColumnNamesWithAssociations;
    private WhereClause where;

    public static <T> Select from(PersistentClass<T> persistentClass, Metadata metadata) {
        return new Select(
                persistentClass.getTableName(),
                persistentClass.getPrimaryKeyName(),
                persistentClass.getGeneralColumnNames(),
                metadata.getAllColumnNamesWithAssociations(persistentClass)
        );
    }

    private Select(String tableName, String primaryKeyColumnName, List<String> generalEntityColumnNames,
                   List<String> allColumnNamesWithAssociations) {
        this.tableName = tableName;
        this.primaryKeyColumnName = primaryKeyColumnName;
        this.generalEntityColumnNames = generalEntityColumnNames;
        this.allColumnNamesWithAssociations = allColumnNamesWithAssociations;
        this.where = null;
    }

    public Select where(WhereMap whereMap) {
        this.where = WhereClause.from(whereMap, allColumnNamesWithAssociations);
        return this;
    }

    public Select id(Long id) {
        return where(WhereMap.of("id", id));
    }

    public Select ids(List<Long> ids) {
        return where(WhereMap.of("id", ids));
    }

    public String buildQuery() {
        StringJoiner query = new StringJoiner(" ")
                .add("SELECT")
                .add(joinAllColumnNames())
                .add("FROM").add(tableName);

        if (where != null) {
            String whereClause = where.toQuery();
            query.add(whereClause);
        }

        return query.toString();
    }

    private String joinAllColumnNames() {
        List<String> columns = new ArrayList<>();
        columns.add(primaryKeyColumnName);
        columns.addAll(generalEntityColumnNames);

        return String.join(COLUMNS_DELIMITER, columns);
    }
}
