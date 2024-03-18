package boot;

import persistence.entity.EntityManager;

public interface EntityManagerFactory {

    EntityManager openSession();

    EntityManager getCurrentSession();
}
