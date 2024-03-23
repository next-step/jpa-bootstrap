package persistence.bootstrap;

import database.mapping.Association;
import persistence.entity.context.PersistentClass;

import java.util.List;

public interface Metadata {
    void register(Class<?> entityClass);

    <T> PersistentClass<T> getPersistentClass(Class<T> clazz);

    List<Class<?>> getEntityClasses();

    Long getRowId(Object entity);

    <T> List<Association> getAssociationsRelatedTo(PersistentClass<T> persistentClass);

    <T> List<String> getAllColumnNamesWithAssociations(PersistentClass<T> persistentClass);

}
