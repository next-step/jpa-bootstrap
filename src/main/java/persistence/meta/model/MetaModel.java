package persistence.meta.model;

import persistence.entity.persistentcontext.EntityPersister;
import persistence.meta.MetaEntity;

public interface MetaModel {
  <T> EntityPersister<T> getPersister(Class<T> clazz);
}
