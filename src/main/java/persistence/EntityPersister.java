package persistence;

import boot.Metamodel;
import builder.dml.EntityData;
import builder.dml.JoinEntityData;
import builder.dml.builder.*;
import database.H2DBConnection;
import jdbc.JdbcTemplate;
import org.h2.command.dml.Insert;

import java.sql.SQLException;

public class EntityPersister {

    private static final String DOT = ".";
    private final JdbcTemplate jdbcTemplate;
    private final DMLQueryBuilder dmlQueryBuilder;
    private Metamodel metamodel;
    private Class<?> entityClass;


    public EntityPersister(JdbcTemplate jdbcTemplate, Metamodel metamodel, DMLQueryBuilder dmlQueryBuilder) {
        this.jdbcTemplate = jdbcTemplate;
        this.metamodel = metamodel;
        this.dmlQueryBuilder = dmlQueryBuilder;
    }

    public EntityPersister(Class<?> entityClass, JdbcTemplate jdbcTemplate, DMLQueryBuilder dmlQueryBuilder) {
        this.jdbcTemplate = jdbcTemplate;
        this.entityClass = entityClass;
        this.dmlQueryBuilder = dmlQueryBuilder;
    }

    //데이터를 반영한다.
    public void persist(EntityData entityData) {
        InsertQueryBuilder insertQueryBuilder = (InsertQueryBuilder) dmlQueryBuilder.query(BuilderName.INSERT);
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
        UpdateQueryBuilder updateQueryBuilder = (UpdateQueryBuilder) dmlQueryBuilder.query(BuilderName.UPDATE);
        jdbcTemplate.execute(updateQueryBuilder.buildQuery(entityData));
    }

    //데이터를 제거한다.
    public void remove(EntityData entityData) {
        DeleteQueryBuilder deleteQueryBuilder = (DeleteQueryBuilder) dmlQueryBuilder.query(BuilderName.DELETE);
        jdbcTemplate.execute(deleteQueryBuilder.buildQuery(entityData));
    }

}
