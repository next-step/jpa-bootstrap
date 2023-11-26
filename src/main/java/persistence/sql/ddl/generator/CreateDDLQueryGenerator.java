package persistence.sql.ddl.generator;


import persistence.sql.dialect.ColumnType;
import persistence.sql.schema.meta.EntityClassMappingMeta;

public class CreateDDLQueryGenerator {

    public static final String CREATE_TABLE_FORMAT = "CREATE TABLE %s (%s);";

    public CreateDDLQueryGenerator() {
    }

    public String create(EntityClassMappingMeta entityClassMappingMeta) {
        return String.format(CREATE_TABLE_FORMAT, entityClassMappingMeta.tableClause(), entityClassMappingMeta.fieldClause());
    }
}
