package jdbc;

import persistence.meta.Metamodel;
import persistence.sql.definition.TableDefinition;

import java.util.HashMap;
import java.util.Map;

public class RowMapperFactory {
    private final Map<Class<?>, EagerFetchRowMapper<?>> eagerFetchRowMappers;
    private final Map<Class<?>, LazyFetchRowMapper<?>> lazyFetchRowMappers;

    private RowMapperFactory() {
        this.eagerFetchRowMappers = new HashMap<>();
        this.lazyFetchRowMappers = new HashMap<>();
    }

    private static class InstanceHolder {
        private static final RowMapperFactory INSTANCE = new RowMapperFactory();
    }

    public static RowMapperFactory getInstance() {
        return InstanceHolder.INSTANCE;
    }

    @SuppressWarnings("unchecked")
    public <T> RowMapper<T> getRowMapper(Class<T> targetClass, Metamodel metamodel, JdbcTemplate jdbcTemplate) {
        RowMapper<T> rowMapper = findCachedRowMapper(targetClass);
        if (rowMapper != null) {
            return rowMapper;
        }
        final TableDefinition tableDefinition = metamodel.getTableDefinition(targetClass);
        for (var association : tableDefinition.getAssociations()) {
            if (association.isEager()) {
                return (RowMapper<T>) eagerFetchRowMappers.computeIfAbsent(targetClass,
                        k -> new EagerFetchRowMapper<>(targetClass,
                                metamodel.getTableDefinition(targetClass),
                                metamodel.getTableDefinition(association.getAssociatedEntityClass())
                        ));
            }
        }
        return (RowMapper<T>) lazyFetchRowMappers.computeIfAbsent(targetClass,
                k -> new LazyFetchRowMapper<>(targetClass,
                        jdbcTemplate,
                        metamodel
                ));
    }

    @SuppressWarnings("unchecked")
    private <T> RowMapper<T> findCachedRowMapper(Class<T> targetClass) {
        if (eagerFetchRowMappers.containsKey(targetClass)) {
            return (RowMapper<T>) eagerFetchRowMappers.get(targetClass);
        }
        if (lazyFetchRowMappers.containsKey(targetClass)) {
            return (RowMapper<T>) lazyFetchRowMappers.get(targetClass);
        }
        return null;
    }
}
