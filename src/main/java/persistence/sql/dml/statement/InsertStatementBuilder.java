package persistence.sql.dml.statement;

import java.util.List;
import java.util.stream.Collectors;
import persistence.sql.schema.meta.ColumnMeta;
import persistence.sql.schema.meta.EntityClassMappingMeta;
import persistence.sql.schema.meta.EntityObjectMappingMeta;

public class InsertStatementBuilder {

    private static final String INSERT_FORMAT = "INSERT INTO %s (%s) values (%s)";
    private static final String INSERT_RETURNING_FORMAT = "SELECT * FROM FINAL TABLE (%s)";

    public InsertStatementBuilder() {
    }

    public String insert(Object object, EntityClassMappingMeta entityClassMappingMeta) {
        final EntityObjectMappingMeta entityObjectMappingMeta = EntityObjectMappingMeta.of(object, entityClassMappingMeta);

        return String.format(INSERT_FORMAT,
            entityClassMappingMeta.tableClause(),
            columnClause(entityObjectMappingMeta),
            valueClause(entityObjectMappingMeta)
        );
    }

    public String insertReturning(Object object, EntityClassMappingMeta entityClassMappingMeta) {
        final EntityObjectMappingMeta entityObjectMappingMeta = EntityObjectMappingMeta.of(object, entityClassMappingMeta);

        final String insertSql = String.format(INSERT_FORMAT,
            entityClassMappingMeta.tableClause(),
            columnClause(entityObjectMappingMeta),
            valueClause(entityObjectMappingMeta)
        );

        return String.format(INSERT_RETURNING_FORMAT, insertSql);
    }

    private String columnClause(EntityObjectMappingMeta entityObjectMappingMeta) {
        final List<String> columnNameList = entityObjectMappingMeta.getColumnMetaList().stream()
            .filter(columnMeta -> !columnMeta.isPrimaryKey())
            .map(ColumnMeta::getColumnName)
            .collect(Collectors.toList());

        return String.join(", ", columnNameList);
    }

    private String valueClause(EntityObjectMappingMeta entityObjectMappingMeta) {
        final List<String> formattedValueList = entityObjectMappingMeta.getMetaEntryList().stream()
            .filter(entry -> !entry.getKey().isPrimaryKey())
            .map(entry -> entry.getValue().getFormattedValue())
            .collect(Collectors.toList());

        return String.join(", ", formattedValueList);
    }
}
