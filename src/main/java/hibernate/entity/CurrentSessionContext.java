package hibernate.entity;

public interface CurrentSessionContext {

    EntityManager currentSession();
}
