package boot;

import persistence.entity.EntityManager;

public interface EntityManagerFactory {

    EntityManager openEntityManager();

    EntityManager getCurrentEntityManager();
}
