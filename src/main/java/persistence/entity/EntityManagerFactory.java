package persistence.entity;


import java.sql.Connection;
import persistence.meta.MetaModel;


public class EntityManagerFactory {
    private final MetaModel metaModel;
    private EntityManagerFactory(MetaModel metaModel) {
        this.metaModel = metaModel;
    }

    public static EntityManagerFactory create(MetaModel metaModel) {
        return new EntityManagerFactory(metaModel);
    }

    public EntityManager createEntityManager(Connection connection) {
        return SimpleEntityManager.create(metaModel, connection);
    }
}
