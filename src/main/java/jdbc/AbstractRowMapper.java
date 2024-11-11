package jdbc;

import common.AliasRule;
import common.ReflectionFieldAccessUtils;
import persistence.entity.EntityPersister;
import persistence.sql.definition.ColumnDefinitionAware;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class AbstractRowMapper<T> implements RowMapper<T> {
    private final Class<T> clazz;
    private final EntityPersister entityPersister;

    protected AbstractRowMapper(Class<T> clazz, EntityPersister entityPersister) {
        this.clazz = clazz;
        this.entityPersister = entityPersister;
    }

    protected abstract void setAssociation(ResultSet resultSet, T instance) throws NoSuchFieldException, SQLException;

    @Override
    @SuppressWarnings("unchecked")
    public T mapRow(ResultSet resultSet) throws SQLException {
        try {
            final T instance = (T) newInstance(clazz);
            setColumns(resultSet, entityPersister, instance);
            setAssociation(resultSet, instance);
            return instance;
        } catch (ReflectiveOperationException e) {
            throw new SQLException("Failed to map row to " + clazz.getName(), e);
        }
    }

    protected Object newInstance(Class<?> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Cannot create new instance of " + clazz.getName(), e);
        }
    }

    protected void setColumns(ResultSet resultSet, EntityPersister entityPersister,
                              Object instance) throws NoSuchFieldException, SQLException {

        final Class<?> entityClass = entityPersister.getEntityClass();

        for (ColumnDefinitionAware column : entityPersister.getColumns()) {
            final String databaseColumnName = column.getDatabaseColumnName();
            final Field instanceField = entityClass.getDeclaredField(column.getEntityFieldName());
            final Object result = resultSet.getObject(
                    AliasRule.with(entityPersister.getTableName(), databaseColumnName));

            ReflectionFieldAccessUtils.accessAndSet(instance, instanceField, result);
        }
    }
}
