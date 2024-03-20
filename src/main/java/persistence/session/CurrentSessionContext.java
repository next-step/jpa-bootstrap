package persistence.session;

import persistence.entity.EntityManager;

public interface CurrentSessionContext {

    EntityManager currentEntityManager();

    void bindEntityManager(EntityManager entityManager);
}
