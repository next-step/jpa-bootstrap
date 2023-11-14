package hibernate.entity;

public interface EntityManagerFactory {

    EntityManager openSession();

    EntityManager currentSession();
}
