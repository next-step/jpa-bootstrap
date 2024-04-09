package persistence.entity.Proxy;

import persistence.entity.loader.CollectionEntityLoader;
import persistence.model.PersistentClass;

import java.util.Collection;

public interface ProxyFactory {

    <T> Collection<T> generateCollectionProxy(final PersistentClass<?> persistentClass, final Class<?> fieldType, final CollectionEntityLoader collectionEntityLoader, final Class<T> joinedEntityClass, final String joinedTableSelectQuery);

}
