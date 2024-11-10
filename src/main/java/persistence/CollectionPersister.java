package persistence;

import builder.dml.JoinEntityData;
import builder.dml.builder.InsertQueryBuilder;
import jdbc.JdbcTemplate;

public class CollectionPersister {

    private final InsertQueryBuilder insertQueryBuilder = new InsertQueryBuilder();
    private final JdbcTemplate jdbcTemplate;

    private Class<?> entityClass;

    public CollectionPersister(Class<?> entityClass, JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.entityClass = entityClass;
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

}
