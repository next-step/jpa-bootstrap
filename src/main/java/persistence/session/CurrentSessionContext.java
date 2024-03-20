package persistence.session;

import persistence.entity.EntityManager;

public interface CurrentSessionContext {

    EntityManager currentEntityManager();

    EntityManager bindEntityManager(EntityManager entityManager);
}
