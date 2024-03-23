package persistence.entity.database;

import database.dialect.Dialect;
import database.sql.ddl.Create;
import database.sql.ddl.Drop;
import database.sql.dml.Delete;
import database.sql.dml.Insert;
import database.sql.dml.Update;
import database.sql.dml.part.ValueMap;
import jdbc.JdbcTemplate;
import persistence.bootstrap.Metadata;
import persistence.entity.context.PersistentClass;

/**
 * 엔터티의 메타데이터와 데이터베이스 매핑 정보를 제공하고,
 * 변경된 엔터티를 데이터베이스에 동기화하는 역할
 */
public class EntityPersister<T> {
    private final PersistentClass<T> persistentClass;
    private final Metadata metadata;
    private final JdbcTemplate jdbcTemplate;
    private final Dialect dialect;

    public EntityPersister(PersistentClass<T> persistentClass, Metadata metadata, JdbcTemplate jdbcTemplate,
                           Dialect dialect) {
        this.persistentClass = persistentClass;
        this.metadata = metadata;
        this.jdbcTemplate = jdbcTemplate;
        this.dialect = dialect;
    }

    public Long insert(Object entity) {
        Long id = metadata.getRowId(entity);
        checkGenerationStrategy(persistentClass.requiresIdWhenInserting(), id, persistentClass.getMappedClassName());
        id = persistentClass.requiresIdWhenInserting() ? id : null;

        String query = Insert.from(persistentClass)
                .id(id)
                .valuesFromEntity(entity).toQueryString();
        return jdbcTemplate.execute(query);
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

    public void updateEntity(Long id, Object entity) {
        String query = Update.from(persistentClass)
                .changesFromEntity(entity)
                .byId(id)
                .buildQuery();
        jdbcTemplate.execute(query);
    }

    public void delete(Long id) {
        String query = Delete.from(persistentClass, metadata)
                .id(id)
                .buildQuery();
        jdbcTemplate.execute(query);
    }

    public void createTable() {
        String query = Create.from(persistentClass, metadata, dialect).buildQuery();
        jdbcTemplate.execute(query);
    }

    public void dropTable(boolean ifExists) {
        String query = Drop.from(persistentClass).ifExists(ifExists).buildQuery();
        jdbcTemplate.execute(query);
    }
}
