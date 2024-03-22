package persistence.bootstrap;

import database.dialect.Dialect;
import jdbc.JdbcTemplate;
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
        MetadataImpl metadata = new MetadataImpl();

        components.forEach(componentClass1 -> {
            Class<T> componentClass = (Class<T>) componentClass1;
            EntityLoader<T> entityLoader = new EntityLoader<>(componentClass, jdbcTemplate, dialect);
            EntityPersister<T> entityPersister = new EntityPersister<>(componentClass, jdbcTemplate);
            CollectionLoader<T> collectionLoader = new CollectionLoader<>(componentClass, jdbcTemplate, dialect);
            metadata.register(componentClass, entityPersister, entityLoader, collectionLoader);
        });

        return metadata;
    }
}
