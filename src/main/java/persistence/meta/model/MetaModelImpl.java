package persistence.meta.model;

import java.util.Map;
import java.util.Optional;
import persistence.entity.persistentcontext.EntityPersister;

public class MetaModelImpl implements MetaModel{
  private final Map<Class<?>, EntityPersister<?>> persisterMap;

  public MetaModelImpl(Map<Class<?>, EntityPersister<?>> persisterMap) {
    this.persisterMap = persisterMap;
  }

  @Override
  public <T> EntityPersister<T> getPersister(Class<T> clazz) {
    Optional<EntityPersister<T>> entityPersister = Optional.ofNullable(
        (EntityPersister<T>) persisterMap.get(clazz));

    return entityPersister.orElseThrow(() -> new IllegalStateException("해당 persiter는 mappings에 존재하지 않습니다."));
  }

}
