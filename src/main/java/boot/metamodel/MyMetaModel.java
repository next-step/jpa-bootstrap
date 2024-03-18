package boot.metamodel;

import boot.EntityBinder;
import jdbc.JdbcTemplate;
import persistence.entity.*;

import java.util.Map;
import java.util.stream.Collectors;

public class MyMetaModel implements MetaModel {
    private final Map<Class<?>, EntityMeta<?>> models;
    private final Map<Class<?>, EntityPersister<?>> persisters;
    private final Map<Class<?>, EntityLoader<?>> loaders;

    public MyMetaModel(JdbcTemplate jdbcTemplate) {
        this.models = EntityBinder.bind();
        this.persisters = models.keySet()
                .stream()
                .collect(Collectors.toMap(clazz -> clazz, clazz -> new MyEntityPersister<>(jdbcTemplate, models.get(clazz))));
        this.loaders = models.keySet()
                .stream()
                .collect(Collectors.toMap(clazz -> clazz, clazz -> new MyEntityLoader<>(jdbcTemplate, models.get(clazz))));
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

    @Override
    @SuppressWarnings("unchecked")
    public <T> EntityMeta<T> getEntityMetaFrom(T entity) {
        return (EntityMeta<T>) models.get(entity.getClass());
    }
}
