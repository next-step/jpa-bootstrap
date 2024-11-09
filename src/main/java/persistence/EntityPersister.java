package persistence;

import boot.Metamodel;
import builder.dml.EntityData;
import builder.dml.JoinEntityData;
import builder.dml.builder.DeleteQueryBuilder;
import builder.dml.builder.InsertQueryBuilder;
import builder.dml.builder.UpdateQueryBuilder;
import database.H2DBConnection;
import jdbc.JdbcTemplate;

import java.sql.SQLException;

public class EntityPersister {

    private static final String DOT = ".";

    private final InsertQueryBuilder insertQueryBuilder = new InsertQueryBuilder();
    private final UpdateQueryBuilder updateQueryBuilder = new UpdateQueryBuilder();
    private final DeleteQueryBuilder deleteQueryBuilder = new DeleteQueryBuilder();
    private final JdbcTemplate jdbcTemplate;
    private Metamodel metamodel;
    private Class<?> entityClass;


    public EntityPersister(JdbcTemplate jdbcTemplate, Metamodel metamodel) {
        this.jdbcTemplate = jdbcTemplate;
        this.metamodel = metamodel;
    }

    public EntityPersister(Class<?> entityClass, JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.entityClass = entityClass;
    }

    //데이터를 반영한다.
    public void persist(EntityData entityData) {
        jdbcTemplate.execute(insertQueryBuilder.buildQuery(entityData.getTableName(), entityData.getEntityColumn()));
        if (entityData.checkJoin()) {
            joinPersist(entityData);
        }
    }

    private void joinPersist(EntityData entityData) {
        entityData.getJoinEntity().getJoinEntityData()
                .forEach(joinEntityData ->
                        this.metamodel.collectionPersister(
                                entityData.getClazz().getSimpleName() +
                                        DOT +
                                        joinEntityData.getClazz().getSimpleName()).persist(joinEntityData)
                );
    }

    //데이터를 수정한다.
    public void merge(EntityData entityData) {
        jdbcTemplate.execute(updateQueryBuilder.buildQuery(entityData));
    }

    //데이터를 제거한다.
    public void remove(EntityData entityData) {
        jdbcTemplate.execute(deleteQueryBuilder.buildQuery(entityData));
    }

}
