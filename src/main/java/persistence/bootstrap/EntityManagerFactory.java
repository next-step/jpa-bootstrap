package persistence.bootstrap;

import persistence.entitymanager.EntityManager;

public interface EntityManagerFactory {
    EntityManager openSession();
}
