package persistence.entity.loader;

import jdbc.JdbcTemplate;
import persistence.core.EntityMetadataProvider;
import persistence.sql.dml.DmlGenerator;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EntityLoaders {

    private final Map<Class<?>, EntityLoader<?>> cache;

    public EntityLoaders(final EntityMetadataProvider entityMetadataProvider,
                         final DmlGenerator dmlGenerator,
                         final JdbcTemplate jdbcTemplate) {
        this.cache = createEntityLoaders(entityMetadataProvider, dmlGenerator, jdbcTemplate);
    }

    private Map<Class<?>, EntityLoader<?>> createEntityLoaders(final EntityMetadataProvider entityMetadataProvider,
                                                               final DmlGenerator dmlGenerator,
                                                               final JdbcTemplate jdbcTemplate) {
        return entityMetadataProvider
                .getAllEntityClasses()
                .stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        clazz -> EntityLoader.of(entityMetadataProvider.getEntityMetadata(clazz), dmlGenerator, jdbcTemplate)
                ));
    }


    @SuppressWarnings("unchecked")
    public <T> EntityLoader<T> getEntityLoader(final Class<T> clazz) {
        return (EntityLoader<T>) cache.get(clazz);
    }

}
