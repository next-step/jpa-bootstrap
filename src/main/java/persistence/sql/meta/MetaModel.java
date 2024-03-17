package persistence.sql.meta;

import persistence.entity.loader.EntityLoader;
import persistence.entity.persister.EntityPersister;

public interface MetaModel {

    <T> EntityPersister<T> getEntityPersister(Class<T> t);

    <T> EntityLoader<T> getEntityLoader(Class<T> t);
}
