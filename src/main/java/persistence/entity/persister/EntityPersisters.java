package persistence.entity.persister;

import jdbc.JdbcTemplate;
import persistence.core.EntityMetadataProvider;
import persistence.core.EntityScanner;
import persistence.sql.dml.DmlGenerator;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EntityPersisters {
    private final Map<Class<?>, EntityPersister> cache;

    public EntityPersisters(final EntityMetadataProvider entityMetadataProvider,
                            final EntityScanner entityScanner,
                            final DmlGenerator dmlGenerator,
                            final JdbcTemplate jdbcTemplate) {
        this.cache = initPersisters(entityMetadataProvider, entityScanner.getEntityClasses(), dmlGenerator, jdbcTemplate);
    }

    private Map<Class<?>, EntityPersister> initPersisters(final EntityMetadataProvider entityMetadataProvider,
                                                          final List<Class<?>> entityClasses,
                                                          final DmlGenerator dmlGenerator,
                                                          final JdbcTemplate jdbcTemplate) {
        return entityClasses.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        clazz -> EntityPersister.of(entityMetadataProvider.getEntityMetadata(clazz), dmlGenerator, jdbcTemplate)
                ));
    }

    public EntityPersister getEntityPersister(final Class<?> clazz) {
        return cache.get(clazz);
    }

}
