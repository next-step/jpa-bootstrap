package persistence.bootstrap;

import persistence.entity.context.EntityKey;
import persistence.entity.context.PersistentClass;

import java.util.List;

public interface Metadata {
    void register(Class<?> entityClass);

    <T> PersistentClass<T> getPersistentClass(Class<T> clazz);

    List<Class<?>> getEntityClasses();

    Long getRowId(Object entity);

    <T> List<String> getAllColumnNamesWithAssociations(PersistentClass<T> persistentClass);

    <T> EntityKey entityKeyOf(PersistentClass<T> persistentClass, Long id);

    EntityKey entityKeyOfObject(Object entity);
}
