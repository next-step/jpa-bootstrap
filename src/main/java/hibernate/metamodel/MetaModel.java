package hibernate.metamodel;

import hibernate.entity.EntityLoader;
import hibernate.entity.EntityPersister;
import hibernate.entity.meta.column.EntityColumn;

public interface MetaModel {

    EntityColumn getEntityId(Class<?> clazz);

    <T> EntityPersister<T> getEntityPersister(Class<T> clazz);

    <T> EntityLoader<T> getEntityLoader(Class<T> clazz);
}
