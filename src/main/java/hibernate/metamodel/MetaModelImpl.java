package hibernate.metamodel;

import hibernate.binder.AnnotationBinder;
import hibernate.entity.EntityLoader;
import hibernate.entity.EntityPersister;
import hibernate.entity.meta.EntityClass;
import hibernate.entity.meta.column.EntityColumn;
import jdbc.JdbcTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MetaModelImpl implements MetaModel {

    private final Map<Class<?>, EntityClass<?>> entityClassMap;
    private final Map<Class<?>, EntityPersister<?>> entityPersisterMap;
    private final Map<Class<?>, EntityLoader<?>> entityLoaderMap;

    private MetaModelImpl(
            final Map<Class<?>, EntityClass<?>> entityClassMap,
            final Map<Class<?>, EntityPersister<?>> entityPersisterMap,
            final Map<Class<?>, EntityLoader<?>> entityLoaderMap
    ) {
        this.entityClassMap = entityClassMap;
        this.entityPersisterMap = entityPersisterMap;
        this.entityLoaderMap = entityLoaderMap;
    }

    public static MetaModel createPackageMetaModel(final String packageName, final JdbcTemplate jdbcTemplate) {
        List<Class<?>> classes = AnnotationBinder.parseEntityClasses(packageName);
        Map<Class<?>, EntityClass<?>> entityClassMap = classes
                .stream()
                .collect(Collectors.toMap(clazz -> clazz, EntityClass::new));
        Map<Class<?>, EntityPersister<?>> entityPersisterMap = classes
                .stream()
                .collect(Collectors.toMap(clazz -> clazz, clazz -> new EntityPersister<>(jdbcTemplate, entityClassMap.get(clazz))));
        Map<Class<?>, EntityLoader<?>> entityLoaderMap = classes
                .stream()
                .collect(Collectors.toMap(clazz -> clazz, clazz -> new EntityLoader<>(jdbcTemplate, entityClassMap.get(clazz))));
        return new MetaModelImpl(entityClassMap, entityPersisterMap, entityLoaderMap);
    }

    @Override
    public EntityColumn getEntityId(final Class<?> clazz) {
        if (entityClassMap.containsKey(clazz)) {
            return entityClassMap.get(clazz)
                    .getEntityId();
        }
        throw new IllegalArgumentException("해당 클래스는 엔티티 클래스가 아닙니다.");
    }

    @Override
    public <T> EntityPersister<T> getEntityPersister(Class<T> clazz) {
        if (entityPersisterMap.containsKey(clazz)) {
            return (EntityPersister<T>) entityPersisterMap.get(clazz);
        }
        throw new IllegalArgumentException("해당 클래스는 엔티티 클래스가 아닙니다.");
    }

    @Override
    public <T> EntityLoader<T> getEntityLoader(Class<T> clazz) {
        if (entityPersisterMap.containsKey(clazz)) {
            return (EntityLoader<T>) entityLoaderMap.get(clazz);
        }
        throw new IllegalArgumentException("해당 클래스는 엔티티 클래스가 아닙니다.");
    }
}
