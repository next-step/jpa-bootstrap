package persistence.core;

import persistence.exception.PersistenceException;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EntityMetadataProvider {

    private final Map<Class<?>, EntityMetadata<?>> cache;

    private EntityMetadataProvider(final EntityScanner entityScanner) {
        this.cache = entityScanner.getEntityClasses()
                .stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        EntityMetadata::from
                ));
    }

    public static EntityMetadataProvider from(final EntityScanner entityScanner) {
        return new EntityMetadataProvider(entityScanner);
    }

    @SuppressWarnings("unchecked")
    public <T> EntityMetadata<T> getEntityMetadata(final Class<T> clazz) {
        final EntityMetadata<?> entityMetadata = cache.get(clazz);
        if (Objects.isNull(entityMetadata)) {
            throw new PersistenceException("EntityMetadata 가 초기화 되지 않았습니다.");
        }
        return (EntityMetadata<T>) entityMetadata;
    }


    public Set<EntityMetadata<?>> getOneToManyAssociatedEntitiesMetadata(final EntityMetadata<?> entityMetadata) {
        return cache.values().stream()
                .filter(metadata -> metadata.hasOneToManyAssociatedOf(entityMetadata))
                .collect(Collectors.toUnmodifiableSet());
    }

}
