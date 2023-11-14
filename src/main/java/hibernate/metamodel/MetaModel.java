package hibernate.metamodel;

import hibernate.entity.EntityLoader;
import hibernate.entity.EntityPersister;
import hibernate.entity.meta.EntityClass;

import java.util.Map;

public interface MetaModel {

    Map<Class<?>, EntityClass<?>> getEntityClasses();

    <T> EntityClass<T> getEntityClass(Class<T> clazz);

    <T> EntityPersister<T> getEntityPersister(Class<T> clazz);

    <T> EntityLoader<T> getEntityLoader(Class<T> clazz);
}
