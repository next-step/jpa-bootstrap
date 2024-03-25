package persistence.entitymanager;

import persistence.entity.database.CollectionLoader;
import persistence.entity.database.EntityLoader;
import persistence.entity.database.EntityPersister;

public interface SessionContract {
    <T> EntityPersister<T> getEntityPersister(Class<T> clazz);

    <T> EntityLoader<T> getEntityLoader(Class<T> clazz);

    <T> CollectionLoader<T> getCollectionLoader(Class<T> clazz);
}
