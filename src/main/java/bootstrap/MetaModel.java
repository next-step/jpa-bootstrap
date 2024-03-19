package bootstrap;

import persistence.entity.EntityLoader;
import persistence.entity.EntityPersister;

public interface MetaModel {

    EntityPersister getEntityPersister(Class<?> clazz);

    EntityLoader getEntityLoader(Class<?> clazz);
}
