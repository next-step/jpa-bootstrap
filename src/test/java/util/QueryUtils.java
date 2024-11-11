package util;

import database.H2ConnectionFactory;
import jdbc.JdbcTemplate;
import persistence.dialect.H2Dialect;
import persistence.meta.EntityTable;
import persistence.sql.ddl.CreateQuery;
import persistence.sql.ddl.DropQuery;

public class QueryUtils {
    private QueryUtils() {
        throw new AssertionError();
    }

    public static void createTable(Class<?> entityType) {
        final EntityTable entityTable = new EntityTable(entityType);
        final CreateQuery createQuery = new CreateQuery(entityTable, new H2Dialect());
        final JdbcTemplate jdbcTemplate  = new JdbcTemplate(H2ConnectionFactory.getConnection());
        jdbcTemplate.execute(createQuery.create());
    }

    public static void createTable(Class<?> entityType, Class<?> parentEntityType) {
        final EntityTable entityTable = new EntityTable(entityType);
        final EntityTable parentEntityTable = new EntityTable(parentEntityType);
        final CreateQuery createQuery = new CreateQuery(entityTable, new H2Dialect());
        final JdbcTemplate jdbcTemplate  = new JdbcTemplate(H2ConnectionFactory.getConnection());
        jdbcTemplate.execute(createQuery.create(parentEntityTable));
    }

    public static void dropTable(Class<?> entityType) {
        final EntityTable entityTable = new EntityTable(entityType);
        final DropQuery dropQuery = new DropQuery(entityTable.getTableName());
        final JdbcTemplate jdbcTemplate  = new JdbcTemplate(H2ConnectionFactory.getConnection());
        jdbcTemplate.execute(dropQuery.drop());
    }
}
