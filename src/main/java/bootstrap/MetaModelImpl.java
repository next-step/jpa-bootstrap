package bootstrap;

import jdbc.JdbcTemplate;
import persistence.entity.EntityLoader;
import persistence.entity.EntityLoaderImpl;
import persistence.entity.EntityPersister;
import persistence.entity.EntityPersisterImpl;
import persistence.sql.dialect.Dialect;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MetaModelImpl implements MetaModel {
    private final Map<Class<?>, EntityPersister> entityPersisterMap = new ConcurrentHashMap<>();
    private final Map<Class<?>, EntityLoader> entityLoaderMap = new ConcurrentHashMap<>();
    private final Binder binder;

    public MetaModelImpl(JdbcTemplate jdbcTemplate, Dialect dialect, String basePackage){
        this.binder = new EntityBinder();
        List<Class<?>> classes = binder.bind(basePackage);

        classes.stream()
                .map(clazz -> {
                    entityPersisterMap.put(clazz, new EntityPersisterImpl(jdbcTemplate, dialect));
                    return entityLoaderMap.put(clazz, new EntityLoaderImpl(jdbcTemplate));
                });
    }

    @Override
    public Map<Class<?>, EntityPersister> getEntityPersisterMap() {
        return entityPersisterMap;
    }

    @Override
    public Map<Class<?>, EntityLoader> getEntityLoaderMap() {
        return entityLoaderMap;
    }
}
