package bootstrap;

import persistence.entity.EntityLoader;
import persistence.entity.EntityPersister;

import java.util.Map;

public interface MetaModel {
    Map<Class<?>, EntityPersister> getEntityPersisterMap();

    Map<Class<?>, EntityLoader> getEntityLoaderMap();
}
