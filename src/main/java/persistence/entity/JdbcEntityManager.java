package persistence.entity;

import java.util.List;
import java.util.Optional;
import persistence.entity.entry.EntityEntry;
import persistence.entity.entry.EntityStatus;
import persistence.entity.persistentcontext.EntityPersister;
import persistence.entity.persistentcontext.PersistenceContext;
import persistence.meta.model.MetaModel;

public class JdbcEntityManager implements EntityManager {

  private final PersistenceContext persistenceContext;
  private final EntityEntry entityEntry;
  private final MetaModel metaModel;

  public JdbcEntityManager(PersistenceContext persistenceContext,
      EntityEntry entityEntry, MetaModel metaModel) {
    this.persistenceContext = persistenceContext;
    this.entityEntry = entityEntry;
    this.metaModel = metaModel;
  }

  @Override
  public <T> T find(Class<T> clazz, Long id) {
    EntityPersister<T> persister = metaModel.getPersister(clazz);

    Optional<T> entity = (Optional<T>) persistenceContext.getEntity(id, clazz);

    entity.ifPresent(obj -> entityEntry.putEntityEntryStatus(obj, EntityStatus.LOADING));

    T foundEntity = entity.orElseGet(() -> {
      Optional<T> obj = persister.load(id);
      obj.ifPresent(presentEntity ->
          putPersistenceContext(id, presentEntity));

      return obj.orElse(null);
    });

    return foundEntity;
  }

  @Override
  public <T> void persist(T entity) {
    EntityPersister<T> persister = (EntityPersister<T>) metaModel.getPersister(entity.getClass());

    entityEntry.putEntityEntryStatus(entity, EntityStatus.SAVING);
    Long assignedId = persister.getEntityId(entity).orElse(-1L);

    if (persister.entityExists(entity)) {
      if (persistenceContext.isSameWithSnapshot(assignedId, entity)) {
        return;
      }
      persister.update(entity);
      putPersistenceContext(assignedId, entity);
      return;
    }

    Long id = persister.insert(entity);
    putPersistenceContext(id, entity);
  }

  @Override
  public <T> void remove(T entity) {
    EntityPersister<T> persister = (EntityPersister<T>) metaModel.getPersister(entity.getClass());

    removePersistenceContext(entity);

    deleteEntity(persister, entity);
  }

  @Override
  public void sync() {
    List<Object> entites = persistenceContext.dirtyCheckedEntities();
    entites
        .forEach(this::persist);
  }

  public <T> void putPersistenceContext(Long id, T entity) {
    persistenceContext.addEntity(id, entity);
    persistenceContext.putDatabaseSnapshot(id, entity);
    entityEntry.putEntityEntryStatus(entity, EntityStatus.MANAGED);
  }

  public <T> void removePersistenceContext(T entity) {
    entityEntry.putEntityEntryStatus(entity, EntityStatus.DELETED);
    persistenceContext.removeEntity(entity);
  }

  public <T> void deleteEntity(EntityPersister<T> persister, T entity) {
    persister.delete(entity);
    entityEntry.putEntityEntryStatus(entity, EntityStatus.GONE);
  }

}
