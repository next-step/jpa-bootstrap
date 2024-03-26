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
    private final Insert insertQuery;
    private final Update updateQuery;
    private final Delete deleteQuery;
    private final Create createQuery;
    private final Drop dropQuery;

    public static <T> EntityPersister<T> from(PersistentClass<T> persistentClass,
                                              Metadata metadata,
                                              JdbcTemplate jdbcTemplate,
                                              Dialect dialect) {
        return new EntityPersister<>(persistentClass,
                                     metadata,
                                     jdbcTemplate,
                                     Insert.from(persistentClass),
                                     Update.from(persistentClass),
                                     Delete.from(persistentClass, metadata),
                                     Create.from(persistentClass, metadata, dialect),
                                     Drop.from(persistentClass));
    }

    private EntityPersister(PersistentClass<T> persistentClass,
                            Metadata metadata,
                            JdbcTemplate jdbcTemplate,
                            Insert insertQuery,
                            Update updateQuery,
                            Delete deleteQuery,
                            Create createQuery,
                            Drop dropQuery) {
        this.persistentClass = persistentClass;
        this.metadata = metadata;
        this.jdbcTemplate = jdbcTemplate;

        this.insertQuery = insertQuery;
        this.updateQuery = updateQuery;
        this.deleteQuery = deleteQuery;
        this.createQuery = createQuery;
        this.dropQuery = dropQuery;
    }

    public Long insert(Object entity) {
        Long id = metadata.getRowId(entity);
        checkGenerationStrategy(id);

        if (!persistentClass.requiresIdWhenInserting()) {
            id = null;
        }

        String query = insertQuery.toSqlFromEntity(id, entity);
        return jdbcTemplate.execute(query);
    }

    private void checkGenerationStrategy(Long id) {
        if (persistentClass.requiresIdWhenInserting() && id == null) {
            throw new PrimaryKeyMissingException(persistentClass.getMappedClassName());
        }
    }

    public void update(Long id, ValueMap changes) {
        String query = updateQuery.toSql(changes, id);
        jdbcTemplate.execute(query);
    }

    public void updateEntity(Long id, Object entity) {
        String query = updateQuery.toSqlFromEntity(entity, id);
        jdbcTemplate.execute(query);
    }

    public void delete(Long id) {
        String query = deleteQuery.toSql(id);
        jdbcTemplate.execute(query);
    }

    public void createTable() {
        String query = createQuery.toSql();
        jdbcTemplate.execute(query);
    }

    public void dropTable(boolean ifExists) {
        String query = dropQuery.toSql(ifExists);
        jdbcTemplate.execute(query);
    }
}
