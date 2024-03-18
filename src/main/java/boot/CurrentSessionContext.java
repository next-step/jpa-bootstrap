package boot;

import persistence.entity.EntityManager;

public interface CurrentSessionContext {

    EntityManager currentSession();

    void bindSession(EntityManager entityManager);
}
