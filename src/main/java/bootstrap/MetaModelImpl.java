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

    public MetaModelImpl(JdbcTemplate jdbcTemplate, Dialect dialect, String basePackage){
        Binder binder = new EntityBinder();
        List<Class<?>> classes = binder.bind(basePackage);

        classes.forEach(clazz -> {
                    entityPersisterMap.put(clazz, new EntityPersisterImpl(jdbcTemplate, dialect));
                    entityLoaderMap.put(clazz, new EntityLoaderImpl(jdbcTemplate));
                });
    }

    @Override
    public EntityPersister getEntityPersister(Class<?> clazz) {
        return entityPersisterMap.get(clazz);
    }

    @Override
    public EntityLoader getEntityLoader(Class<?> clazz) {
        return entityLoaderMap.get(clazz);
    }
}
