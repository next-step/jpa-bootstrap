package persistence.bootstrap;

import database.dialect.Dialect;
import database.mapping.Association;
import persistence.entity.context.PersistentClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * - 각 클래스 metadata 를 여기서 전부 모을 것
 * - sessionfactory 에 metadata 넘길 것
 * -
 */
public class MetadataImpl implements Metadata {
    private final Dialect dialect;
    private final List<Class<?>> entityClasses;
    private final Map<Class<?>, PersistentClass<?>> persistentClassMap;

    public MetadataImpl(Dialect dialect) {
        this.dialect = dialect;
        this.entityClasses = new ArrayList<>();
        this.persistentClassMap = new ConcurrentHashMap<>();
    }

    @Override
    public void register(Class<?> entityClass) {
        this.entityClasses.add(entityClass);
        this.persistentClassMap.put(entityClass, PersistentClass.fromInternal(entityClass, dialect));
    }

    @Override
    public <T> PersistentClass<T> getPersistentClass(Class<T> clazz) {
        //noinspection unchecked
        return (PersistentClass<T>) persistentClassMap.get(clazz);
    }

    @Override
    public List<Class<?>> getEntityClasses() {
        return entityClasses;
    }

    @Override
    public Long getRowId(Object entity) {
        Class<?> clazz = entity.getClass();
        return getPersistentClass(clazz).getRowId(entity);
    }

    @Override
    public <T> List<Association> getAssociationsRelatedTo(PersistentClass<T> persistentClass) {
        return persistentClass.getAssociationsRelatedTo(entityClasses);
    }

    @Override
    public <T> List<String> getAllColumnNamesWithAssociations(PersistentClass<T> persistentClass) {
        return persistentClass.getAllColumnNamesWithAssociations(entityClasses);
    }
}
