package boot;

import persistence.entity.EntityManager;

public interface EntityManagerContext {

    EntityManager currentEntityManager();

    void bindEntityManager(EntityManager entityManager);
}
