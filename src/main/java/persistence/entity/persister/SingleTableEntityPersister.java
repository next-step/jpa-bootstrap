package persistence.entity.persister;

import jdbc.JdbcTemplate;
import persistence.model.EntityIdentifierMapping;
import persistence.model.PersistentClass;
import persistence.model.PersistentClassMapping;
import persistence.sql.dml.Delete;
import persistence.sql.dml.DmlQueryBuilder;
import persistence.sql.dml.Insert;
import persistence.sql.dml.Update;
import persistence.sql.mapping.Table;
import persistence.sql.mapping.TableBinder;

import java.lang.reflect.Field;

public class SingleTableEntityPersister implements EntityPersister {

    private final String name;
    private final PersistentClassMapping persistentClassMapping;
    private final TableBinder tableBinder;
    private final DmlQueryBuilder dmlQueryBuilder;
    private final JdbcTemplate jdbcTemplate;
    private final EntityIdentifierMapping identifierMapping;

    public SingleTableEntityPersister(final String name, final PersistentClassMapping persistentClassMapping, final TableBinder tableBinder, final DmlQueryBuilder dmlQueryBuilder, final JdbcTemplate jdbcTemplate, final PersistentClass<?> persistentClass) {
        this.name = name;
        this.persistentClassMapping = persistentClassMapping;
        this.tableBinder = tableBinder;
        this.dmlQueryBuilder = dmlQueryBuilder;
        this.jdbcTemplate = jdbcTemplate;
        final Field idField = getIdField(persistentClass);
        this.identifierMapping = new EntityIdentifierMapping(persistentClass.getEntityClass(), idField.getName(), idField);
    }

    private Field getIdField(final PersistentClass<?> persistentClass) {
        return persistentClass.getEntityFields()
                .getIdField()
                .getField();
    }

    public String getTargetEntityName() {
        return this.name;
    }

    @Override
    public boolean update(final Object entity) {
        final Table table = tableBinder.createTable(persistentClassMapping.getPersistentClass(entity.getClass()), entity);
        final String updateQuery = dmlQueryBuilder.buildUpdateQuery(new Update(table));

        try {
            jdbcTemplate.execute(updateQuery);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    @Override
    public Object insert(final Object entity) {
        final Table table = tableBinder.createTable(persistentClassMapping.getPersistentClass(entity.getClass()), entity);
        final boolean hasIdentifierKey = table.getPrimaryKey().hasIdentifierKey();

        final String insertQuery = dmlQueryBuilder.buildInsertQuery(new Insert(table));

        Object key = null;

        if (hasIdentifierKey) {
            key = jdbcTemplate.executeWithGeneratedKey(insertQuery);
        } else {
            jdbcTemplate.execute(insertQuery);
        }

        return key;
    }

    @Override
    public void delete(final Object entity) {
        final Table table = tableBinder.createTable(persistentClassMapping.getPersistentClass(entity.getClass()), entity);

        final String deleteQuery = dmlQueryBuilder.buildDeleteQuery(new Delete(table));

        jdbcTemplate.execute(deleteQuery);
    }

    @Override
    public Object getIdentifier(final Object entity) {
        return identifierMapping.getIdentifier(entity);
    }

    @Override
    public void setIdentifier(final Object entity, final Object value) {
        identifierMapping.setIdentifierValue(entity, value);
    }
}
