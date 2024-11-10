package persistence.session;

import common.SqlLogger;
import jdbc.JdbcTemplate;
import persistence.meta.Metadata;
import persistence.sql.ddl.query.CreateTableQueryBuilder;
import persistence.sql.ddl.query.DropQueryBuilder;

public class SchemaManagementToolCoordinator {
    private SchemaManagementToolCoordinator() {
    }

    public static void processCreateTable(final JdbcTemplate jdbcTemplate,
                                          final Metadata metadata) {

        metadata.findTableDefinitions().forEach(table -> {
            final String query = new CreateTableQueryBuilder(
                    table.getEntityClass(),
                    metadata
            ).build();

            SqlLogger.infoCreateTable(query);

            jdbcTemplate.execute(query);
        });
    }

    public static void processDropTable(final JdbcTemplate jdbcTemplate,
                                        final Metadata metadata) {

        metadata.findTableDefinitions().forEach(table -> {
            final String query = new DropQueryBuilder(table.getTableName()).build();

            SqlLogger.infoDropTable(query);

            jdbcTemplate.execute(query);
        });
    }
}
