package persistence.bootstrap;

import jdbc.DefaultRowMapper;
import jdbc.RowMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RowMapperBinder {
    private final Map<String, RowMapper<?>> rowMapperRegistry = new HashMap<>();

    public RowMapperBinder(List<Class<?>> entityTypes) {
        for (Class<?> entityType : entityTypes) {
            final DefaultRowMapper<?> defaultRowMapper = new DefaultRowMapper<>(entityType);
            rowMapperRegistry.put(entityType.getTypeName(), defaultRowMapper);
        }
    }

    public RowMapper<?> getRowMapper(Class<?> entityType) {
        return rowMapperRegistry.get(entityType.getName());
    }
}
