package persistence.entity;


import java.sql.Connection;
import persistence.meta.MetaModel;


public class EntityManagerFactory {

    private final MetaModel metaModel;

    public EntityManagerFactory(MetaModel metaModel) {
        this.metaModel = metaModel;
    }

    public EntityManager openSession(Connection connection) {
        return new SimpleEntityManager(metaModel, connection);
    }
}
