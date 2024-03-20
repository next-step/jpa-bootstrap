package persistence.entity;

public interface EntityManagerFactory {

    EntityManager openSession();
}
