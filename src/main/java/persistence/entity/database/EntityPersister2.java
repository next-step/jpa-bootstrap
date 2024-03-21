package persistence.entity.database;

import database.mapping.EntityMetadata;
import database.mapping.EntityMetadataFactory;
import database.sql.dml.Delete;
import database.sql.dml.Insert;
import database.sql.dml.Update;
import database.sql.dml.part.ValueMap;
import jdbc.JdbcTemplate;

/**
 * 엔터티의 메타데이터와 데이터베이스 매핑 정보를 제공하고,
 * 변경된 엔터티를 데이터베이스에 동기화하는 역할
 */
public class EntityPersister2<T> {
    private final JdbcTemplate jdbcTemplate;

    private final Class<T> clazz;
    private EntityMetadata entityMetadata;

    public EntityPersister2(JdbcTemplate jdbcTemplate, Class<T> clazz) {
        this.jdbcTemplate = jdbcTemplate;
        this.clazz = clazz;
        this.entityMetadata = EntityMetadataFactory.get(clazz);
    }

    public Long insert(Object entity) {
        Long id = entityMetadata.getPrimaryKeyValue(entity);
        checkGenerationStrategy(entityMetadata.requiresIdWhenInserting(), id, entityMetadata.getEntityClassName());
        id = entityMetadata.requiresIdWhenInserting() ? id : null;

        Insert insert = new Insert(entityMetadata.getTableName(),
                                   entityMetadata.getPrimaryKey(),
                                   entityMetadata.getGeneralColumns())
                .id(id)
                .valuesFromEntity(entity);
        return jdbcTemplate.execute(insert.toQueryString());
    }

    private void checkGenerationStrategy(boolean requiresIdWhenInserting, Long id, String entityClassName) {
        if (requiresIdWhenInserting && id == null) {
            throw new PrimaryKeyMissingException(entityClassName);
        }
    }

    public void update(Long id, ValueMap changes) {
        String query = new Update(entityMetadata.getTableName(),
                                  entityMetadata.getGeneralColumns(),
                                  entityMetadata.getPrimaryKey())
                .changes(changes)
                .byId(id)
                .buildQuery();
        jdbcTemplate.execute(query);
    }

    public void update(Long id, Object entity) {
        String query = new Update(entityMetadata.getTableName(),
                                  entityMetadata.getGeneralColumns(),
                                  entityMetadata.getPrimaryKey())
                .changesFromEntity(entity)
                .byId(id)
                .buildQuery();
        jdbcTemplate.execute(query);
    }

    public void delete(Long id) {
        String query = new Delete(entityMetadata.getTableName(),
                                  entityMetadata.getAllColumnNamesWithAssociations(),
                                  entityMetadata.getPrimaryKey()
        )
                .id(id)
                .buildQuery();
        jdbcTemplate.execute(query);
    }
}
