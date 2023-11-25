package persistence.meta.model;

import persistence.entity.persistentcontext.EntityPersister;

public interface MetaModel {
  <T> EntityPersister<T> getPersister(Class<T> clazz);
}
