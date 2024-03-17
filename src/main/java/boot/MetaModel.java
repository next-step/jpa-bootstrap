package boot;

import persistence.entity.EntityLoader;
import persistence.entity.EntityMeta;
import persistence.entity.EntityPersister;

public interface MetaModel {

    <T> EntityPersister<T> getEntityPersister(Class<T> clazz);

    <T> EntityLoader<T> getEntityLoader(Class<T> clazz);

    <T> EntityMeta<T> getEntityMetaFrom(T entity);
}
