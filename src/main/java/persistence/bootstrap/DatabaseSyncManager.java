package persistence.bootstrap;

import jdbc.JdbcTemplate;
import persistence.bootstrap.binder.EntityAssociationBinder;
import persistence.bootstrap.binder.EntityTableBinder;
import persistence.dialect.H2Dialect;
import persistence.meta.EntityAssociation;
import persistence.meta.EntityTable;
import persistence.sql.ddl.CreateQuery;
import persistence.sql.ddl.DropQuery;

import java.util.List;
import java.util.Objects;

public class DatabaseSyncManager {
    private DatabaseSyncManager() {
        throw new AssertionError();
    }

    public static void sync(EntityTableBinder entityTableBinder, EntityAssociationBinder entityAssociationBinder,
                            JdbcTemplate jdbcTemplate) {
        final CreateQuery createQuery = new CreateQuery(new H2Dialect());

        final List<EntityTable> entityTables = entityTableBinder.getAllEntityTables();
        for (EntityTable entityTable : entityTables) {
            final String sql = getSql(entityAssociationBinder, entityTable, createQuery);
            jdbcTemplate.execute(sql);
        }
    }

    public static void clear(EntityTableBinder entityTableBinder, JdbcTemplate jdbcTemplate) {
        final DropQuery dropQuery = new DropQuery();

        final List<EntityTable> entityTables = entityTableBinder.getAllEntityTables();
        for (EntityTable entityTable : entityTables) {
            final String sql = dropQuery.drop(entityTable);
            jdbcTemplate.execute(sql);
        }
    }

    private static String getSql(EntityAssociationBinder entityAssociationBinder, EntityTable entityTable, CreateQuery createQuery) {
        final EntityAssociation entityAssociation = entityAssociationBinder.getEntityAssociation(entityTable.getType());
        if (Objects.nonNull(entityAssociation)) {
            return  createQuery.create(entityTable,entityAssociation.getParentEntityTable());
        }
        return createQuery.create(entityTable);
    }
}
