package persistence.entity.persister;

import jdbc.JdbcTemplate;
import persistence.core.EntityMetadataProvider;
import persistence.sql.dml.DmlGenerator;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EntityPersisters {
    private final Map<Class<?>, EntityPersister> cache;

    public EntityPersisters(final EntityMetadataProvider entityMetadataProvider,
                            final DmlGenerator dmlGenerator,
                            final JdbcTemplate jdbcTemplate) {
        this.cache = initPersisters(entityMetadataProvider, dmlGenerator, jdbcTemplate);
    }

    private Map<Class<?>, EntityPersister> initPersisters(final EntityMetadataProvider entityMetadataProvider,
                                                          final DmlGenerator dmlGenerator,
                                                          final JdbcTemplate jdbcTemplate) {
        return entityMetadataProvider
                .getAllEntityClasses()
                .stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        clazz -> EntityPersister.of(entityMetadataProvider.getEntityMetadata(clazz), dmlGenerator, jdbcTemplate)
                ));
    }

    public EntityPersister getEntityPersister(final Class<?> clazz) {
        return cache.get(clazz);
    }

}
