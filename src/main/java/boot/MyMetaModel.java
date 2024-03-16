package boot;

import jdbc.JdbcTemplate;
import persistence.entity.*;

import java.util.Map;
import java.util.stream.Collectors;

public class MyMetaModel implements MetaModel {
    private static final String BASE_PACKAGE = "domain";

    private final Map<Class<?>, EntityMeta> models;
    private final Map<Class<?>, EntityPersister> persisters;
    private final Map<Class<?>, EntityLoader> loaders;

    public MyMetaModel(JdbcTemplate jdbcTemplate) {
        this.models = EntityBinder.bind(BASE_PACKAGE);
        this.persisters = models.keySet()
                .stream()
                .collect(Collectors.toMap(clazz -> clazz, clazz -> new MyEntityPersister<>(jdbcTemplate, EntityMeta.from(clazz))));
        this.loaders = models.keySet()
                .stream()
                .collect(Collectors.toMap(clazz -> clazz, clazz -> new MyEntityLoader<>(jdbcTemplate, EntityMeta.from(clazz))));
    }

    public Map<Class<?>, EntityMeta> getModels() {
        return models;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> EntityPersister<T> getEntityPersister(Class<T> clazz) {
        return (EntityPersister<T>) persisters.get(clazz);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> EntityLoader<T> getEntityLoader(Class<T> clazz) {
        return (EntityLoader<T>) loaders.get(clazz);
    }
}
