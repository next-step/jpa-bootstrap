package persistence.bootstrap;

import persistence.entity.database.CollectionLoader;
import persistence.entity.database.EntityLoader;
import persistence.entity.database.EntityPersister;

public interface Metamodel {
    <T> EntityPersister<T> getEntityPersister(Class<T> entityClass);

    <T> EntityLoader<T> getEntityLoader(Class<T> entityClass);

    <T> CollectionLoader<T> getCollectionLoader(Class<T> entityClass);
}
