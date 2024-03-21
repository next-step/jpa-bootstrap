package persistence.bootstrap;

import database.dialect.Dialect;
import jdbc.JdbcTemplate;
import persistence.entity.database.CollectionLoader2;
import persistence.entity.database.EntityLoader2;
import persistence.entity.database.EntityPersister2;

import java.util.List;

public class MetadataInitializer {
    private final List<Class<?>> components;

    public MetadataInitializer(List<Class<?>> components) {
        this.components = components;
    }

    public <T> void initialize(JdbcTemplate jdbcTemplate, Dialect dialect) {
        MetadataImpl metadata = new MetadataImpl();

        components.forEach(componentClass1 -> {
            Class<T> componentClass = (Class<T>) componentClass1;
            EntityLoader2<T> entityLoader2 = new EntityLoader2<>(componentClass, jdbcTemplate, dialect);
            EntityPersister2<T> entityPersister2 = new EntityPersister2<>(jdbcTemplate, componentClass);
            CollectionLoader2<T> collectionLoader2 = new CollectionLoader2<>(entityLoader2, jdbcTemplate, dialect, componentClass);
            metadata.register(componentClass, entityPersister2, entityLoader2, collectionLoader2);
        });
    }
}
