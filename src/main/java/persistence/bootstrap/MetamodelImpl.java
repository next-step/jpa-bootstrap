package persistence.bootstrap;

import database.dialect.Dialect;
import jdbc.JdbcTemplate;
import persistence.entity.context.PersistentClass;
import persistence.entity.database.CollectionLoader;
import persistence.entity.database.EntityLoader;
import persistence.entity.database.EntityPersister;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MetamodelImpl implements Metamodel {
    private final Metadata metadata;
    private final JdbcTemplate jdbcTemplate;
    private final Dialect dialect;

    public MetamodelImpl(Metadata metadata, JdbcTemplate jdbcTemplate, Dialect dialect) {
        this.metadata = metadata;
        this.jdbcTemplate = jdbcTemplate;
        this.dialect = dialect;
    }

    private final Map<Class<?>, EntityPersister<?>> entityPersisterMap = new ConcurrentHashMap<>();
    private final Map<Class<?>, EntityLoader<?>> entityLoaderMap = new ConcurrentHashMap<>();
    private final Map<Class<?>, CollectionLoader<?>> collectionLoaderMap = new ConcurrentHashMap<>();

    public void initialize() {
        metadata.getEntityClasses().forEach(mappedClass -> {
            PersistentClass<?> persistentClass = metadata.getPersistentClass((Class<?>) mappedClass);

            entityPersisterMap.put(mappedClass, new EntityPersister<>(persistentClass, metadata, jdbcTemplate, dialect));
            entityLoaderMap.put(mappedClass, new EntityLoader<>(persistentClass, metadata, jdbcTemplate));
            collectionLoaderMap.put(mappedClass, new CollectionLoader<>(persistentClass, metadata, jdbcTemplate, this));
        });
    }

    @Override
    public <T> EntityPersister<T> getEntityPersister(Class<T> entityClass) {
        //noinspection unchecked
        return (EntityPersister<T>) entityPersisterMap.get(entityClass);
    }

    @Override
    public <T> EntityLoader<T> getEntityLoader(Class<T> entityClass) {
        //noinspection unchecked
        return (EntityLoader<T>) entityLoaderMap.get(entityClass);
    }

    @Override
    public <T> CollectionLoader<T> getCollectionLoader(Class<T> entityClass) {
        //noinspection unchecked
        return (CollectionLoader<T>) collectionLoaderMap.get(entityClass);
    }
}
