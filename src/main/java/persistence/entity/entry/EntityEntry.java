package persistence.entity.entry;

public interface EntityEntry {

  void putEntityEntryStatus(Object entity, EntityStatus entityEntryStatus);

  EntityStatus getEntityStatus(Object entity);

}
