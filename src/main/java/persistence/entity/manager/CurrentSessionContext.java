package persistence.entity.manager;

import java.util.Optional;

public interface CurrentSessionContext extends SessionCloseStrategy {
    void open(EntityManager entityManager);

    Optional<EntityManager> getCurrentSession();

}
