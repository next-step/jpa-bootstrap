package persistence.entity;

import java.util.Optional;

public class CurrentSessionContext implements SessionContext {

  private static final ThreadLocal<EntityManager> CONTEXT = new ThreadLocal<>();

  @Override
  public Optional<EntityManager> getEntityManager() {
    return Optional.ofNullable(CONTEXT.get());
  }

  @Override
  public void setEntityManager(EntityManager entityManager) {
    CONTEXT.set(entityManager);
  }

  @Override
  public void clearEntityManager() {
    CONTEXT.remove();
  }
}
