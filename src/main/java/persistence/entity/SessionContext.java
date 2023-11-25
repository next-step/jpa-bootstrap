package persistence.entity;

import java.util.Optional;

public interface SessionContext {

  Optional<EntityManager> getEntityManager();

  void setEntityManager(EntityManager entityManager);

  void clearEntityManager();
}
