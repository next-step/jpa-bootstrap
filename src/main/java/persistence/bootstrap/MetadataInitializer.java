package persistence.bootstrap;

import database.dialect.Dialect;
import jdbc.JdbcTemplate;
import persistence.entity.context.PersistentClass;
import persistence.entity.database.CollectionLoader;
import persistence.entity.database.EntityLoader;
import persistence.entity.database.EntityPersister;

import java.util.List;

public class MetadataInitializer {
    private final List<Class<?>> components;

    public MetadataInitializer(List<Class<?>> components) {
        this.components = components;
    }

    public <T> MetadataImpl initialize(JdbcTemplate jdbcTemplate, Dialect dialect) {
        MetadataImpl metadata = MetadataImpl.INSTANCE;

        metadata.setComponents(components);
        components.forEach(componentClass1 -> {
            PersistentClass<T> persistentClass = metadata.getPersistentClass((Class<T>) componentClass1);
            metadata.register(
                    persistentClass,
                    new EntityPersister<>(persistentClass, jdbcTemplate, components),
                    new EntityLoader<>(persistentClass, jdbcTemplate, dialect, components),
                    new CollectionLoader<>(persistentClass, jdbcTemplate, dialect, components)
            );
        });

        return metadata;
    }
}
