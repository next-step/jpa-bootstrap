package persistence.entity.database;

import database.sql.dml.Delete;
import database.sql.dml.Insert;
import database.sql.dml.Update;
import database.sql.dml.part.ValueMap;
import jdbc.JdbcTemplate;
import persistence.entity.context.PersistentClass;

import java.util.List;

/**
 * 엔터티의 메타데이터와 데이터베이스 매핑 정보를 제공하고,
 * 변경된 엔터티를 데이터베이스에 동기화하는 역할
 */
public class EntityPersister<T> {
    private final JdbcTemplate jdbcTemplate;
    private final PersistentClass<T> persistentClass;
    private final List<Class<?>> entities;

    public EntityPersister(PersistentClass<T> persistentClass, JdbcTemplate jdbcTemplate, List<Class<?>> entities) {
        this.persistentClass = persistentClass;
        this.jdbcTemplate = jdbcTemplate;

        this.entities = entities;
    }

    public Long insert(Object entity) {
        Long id = persistentClass.getPrimaryKeyValue(entity);
        checkGenerationStrategy(persistentClass.requiresIdWhenInserting(), id, persistentClass.getMappedClassName());
        id = persistentClass.requiresIdWhenInserting() ? id : null;

        Insert insert = Insert.from(persistentClass)
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
        String query = Update.from(persistentClass)
                .changes(changes)
                .byId(id)
                .buildQuery();
        jdbcTemplate.execute(query);
    }

    public void update(Long id, Object entity) {
        String query = Update.from(persistentClass)
                .changesFromEntity(entity)
                .byId(id)
                .buildQuery();
        jdbcTemplate.execute(query);
    }

    public void delete(Long id) {
        String query = Delete.from(persistentClass, entities)
                .id(id)
                .buildQuery();
        jdbcTemplate.execute(query);
    }

}
