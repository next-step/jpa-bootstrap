package jdbc;

import common.ReflectionFieldAccessUtils;
import persistence.entity.EntityLazyLoader;
import persistence.meta.Metamodel;
import persistence.proxy.PersistentList;
import persistence.sql.definition.TableAssociationDefinition;
import persistence.sql.definition.TableDefinition;
import persistence.sql.dml.query.SelectQueryBuilder;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class LazyFetchRowMapper<T> extends AbstractRowMapper<T> {
    private final Class<T> clazz;
    private final TableDefinition tableDefinition;
    private final Metamodel metamodel;
    private final JdbcTemplate jdbcTemplate;

    public LazyFetchRowMapper(Class<T> clazz, Metamodel metamodel, JdbcTemplate jdbcTemplate) {
        super(clazz, metamodel);
        this.clazz = clazz;
        this.tableDefinition = new TableDefinition(clazz);
        this.metamodel = metamodel;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    protected void setAssociation(ResultSet resultSet, T instance) throws NoSuchFieldException, SQLException {
        List<TableAssociationDefinition> associations = tableDefinition.getAssociations();
        for (TableAssociationDefinition association : associations) {
            if (association.isEager()) {
                continue;
            }

            final Field collectionField = clazz.getDeclaredField(association.getFieldName());
            List proxy = createProxy(instance, association.getAssociatedEntityClass());
            ReflectionFieldAccessUtils.accessAndSet(instance, collectionField, proxy);
        }
    }

    @SuppressWarnings("unchecked")
    public <E> List<E> createProxy(Object instance, Class<E> elementType) {
        return (List<E>) Proxy.newProxyInstance(
                instance.getClass().getClassLoader(),
                new Class[]{List.class},
                new PersistentList<>(instance, createLazyLoader(elementType))
        );
    }

    private EntityLazyLoader createLazyLoader(Class<?> elementClass) {
        return owner -> {
            final TableDefinition ownerDefinition = new TableDefinition(owner.getClass());

            final String joinColumnName = ownerDefinition.getJoinColumnName(elementClass);
            final Object joinColumnValue = ownerDefinition.getValue(owner, joinColumnName);

            final String query = new SelectQueryBuilder(elementClass, metamodel)
                    .where(joinColumnName, joinColumnValue.toString())
                    .build();

            return jdbcTemplate.query(query,
                    RowMapperFactory.getInstance().createRowMapper(elementClass, metamodel, jdbcTemplate)
            );
        };
    }
}
