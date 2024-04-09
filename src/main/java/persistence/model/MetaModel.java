package persistence.model;

import persistence.entity.loader.EntityLoader;
import persistence.entity.persister.EntityPersister;

public interface MetaModel {

    EntityPersister getEntityDescriptor(final Object entity);

    EntityLoader getEntityLoader();

    PersistentClassMapping getPersistentClassMapping();
}
