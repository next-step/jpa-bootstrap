package jdbc;

import common.ReflectionFieldAccessUtils;
import persistence.entity.EntityLazyLoader;
import persistence.entity.EntityPersister;
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
    private final EntityPersister entityPersister;
    private final JdbcTemplate jdbcTemplate;
    private final Metamodel metamodel;

    public LazyFetchRowMapper(Class<T> clazz,
                              JdbcTemplate jdbcTemplate,
                              Metamodel metamodel) {
        super(clazz, metamodel.findEntityPersister(clazz));
        this.entityPersister = metamodel.findEntityPersister(clazz);
        this.clazz = clazz;
        this.jdbcTemplate = jdbcTemplate;
        this.metamodel = metamodel;
    }

    @Override
    protected void setAssociation(ResultSet resultSet, T instance) throws NoSuchFieldException, SQLException {
        List<TableAssociationDefinition> associations = entityPersister.getAssociations();
        for (TableAssociationDefinition association : associations) {
            if (association.isEager()) {
                continue;
            }

            ReflectionFieldAccessUtils.accessAndSet(instance,
                    clazz.getDeclaredField(association.getFieldName()),
                    createProxy(instance, association.getAssociatedEntityClass())
            );
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
            final String joinColumnName = entityPersister.getJoinColumnName(elementClass);
            final Object joinColumnValue = entityPersister.getValue(owner, joinColumnName);

            final String query = new SelectQueryBuilder(elementClass, metamodel)
                    .where(joinColumnName, joinColumnValue.toString())
                    .build();

            return jdbcTemplate.query(query,
                    RowMapperFactory.getInstance().getRowMapper(elementClass, metamodel, jdbcTemplate)
            );
        };
    }
}
