package persistence;

import builder.dml.JoinEntityData;
import builder.dml.builder.BuilderName;
import builder.dml.builder.DMLQueryBuilder;
import builder.dml.builder.InsertQueryBuilder;
import jdbc.JdbcTemplate;

public class CollectionPersister {

    private final JdbcTemplate jdbcTemplate;
    private final DMLQueryBuilder dmlQueryBuilder;

    private Class<?> entityClass;

    public CollectionPersister(Class<?> entityClass, JdbcTemplate jdbcTemplate, DMLQueryBuilder dmlQueryBuilder) {
        this.jdbcTemplate = jdbcTemplate;
        this.entityClass = entityClass;
        this.dmlQueryBuilder = dmlQueryBuilder;
    }

    public String getSimpleName() {
        return entityClass.getSimpleName();
    }

    public void persist(JoinEntityData joinEntityData) {
        InsertQueryBuilder insertQueryBuilder = (InsertQueryBuilder) dmlQueryBuilder.query(BuilderName.INSERT);
        jdbcTemplate.execute(insertQueryBuilder.buildQuery(
                joinEntityData.getTableName(),
                joinEntityData.getJoinColumnData())
        );
    }

}
