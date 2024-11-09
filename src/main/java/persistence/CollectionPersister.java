package persistence;

import builder.dml.EntityData;
import builder.dml.JoinEntityData;
import builder.dml.builder.InsertQueryBuilder;
import database.H2DBConnection;
import jdbc.JdbcTemplate;

import java.sql.SQLException;

public class CollectionPersister {

    private final InsertQueryBuilder insertQueryBuilder = new InsertQueryBuilder();
    private final JdbcTemplate jdbcTemplate;

    private Class<?> entityClass;

    public CollectionPersister(Class<?> entityClass, JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.entityClass = entityClass;
    }

    public CollectionPersister(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String getSimpleName() {
        return entityClass.getSimpleName();
    }

    public void persist(JoinEntityData joinEntityData) {
        jdbcTemplate.execute(insertQueryBuilder.buildQuery(
                joinEntityData.getTableName(),
                joinEntityData.getJoinColumnData())
        );
    }

    private JdbcTemplate initializeJdbcTemplate() {
        try {
            return new H2DBConnection().start();
        } catch (SQLException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

}
