package persistence;

import builder.dml.EntityData;
import builder.dml.JoinEntityData;
import builder.dml.builder.BuilderName;
import builder.dml.builder.DMLQueryBuilder;
import builder.dml.builder.SelectByIdQueryBuilder;
import jdbc.EntityMapper;
import jdbc.JdbcTemplate;

import java.util.List;

public class EntityLoader {

    private final JdbcTemplate jdbcTemplate;
    private final DMLQueryBuilder dmlQueryBuilder;
    private Class<?> entityClass;

    public EntityLoader(JdbcTemplate jdbcTemplate, DMLQueryBuilder dmlQueryBuilder) {
        this.jdbcTemplate = jdbcTemplate;
        this.dmlQueryBuilder = dmlQueryBuilder;
    }

    public EntityLoader(Class<?> entityClass, JdbcTemplate jdbcTemplate, DMLQueryBuilder dmlQueryBuilder) {
        this.jdbcTemplate = jdbcTemplate;
        this.entityClass = entityClass;
        this.dmlQueryBuilder = dmlQueryBuilder;
    }

    //데이터를 조회한다.
    public <T> T find(Class<T> clazz, Object id) {
        SelectByIdQueryBuilder selectByIdQueryBuilder = (SelectByIdQueryBuilder) dmlQueryBuilder.query(BuilderName.SELECT_BY_ID);
        EntityData entityData = EntityData.createEntityData(clazz, id);
        return jdbcTemplate.queryForObject(selectByIdQueryBuilder.buildQuery(entityData), resultSet -> EntityMapper.mapRow(resultSet, entityData));
    }

    //Lazy 데이터를 전체 조회한다.
    @SuppressWarnings("unchecked")
    public <T> List<T> findByIdLazy(JoinEntityData joinEntityData) {
        SelectByIdQueryBuilder selectByIdQueryBuilder = (SelectByIdQueryBuilder) dmlQueryBuilder.query(BuilderName.SELECT_BY_ID);
        return (List<T>) jdbcTemplate.query(selectByIdQueryBuilder.buildLazyQuery(joinEntityData), resultSet -> EntityMapper.mapRow(resultSet, joinEntityData.getClazz()));
    }

}
