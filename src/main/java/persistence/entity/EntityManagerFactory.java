package persistence.entity;


import java.sql.Connection;
import jdbc.JdbcTemplate;
import persistence.meta.MetaModel;


public class EntityManagerFactory {
    private MetaModel metaModel;
    private EntityManagerFactory(MetaModel metaModel) {
        this.metaModel = metaModel;
    }

    public static EntityManagerFactory create(MetaModel metaModel) {
        return new EntityManagerFactory(metaModel);
    }

    public EntityManager createEntityManager(Connection connection) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(connection);
        EntityPersisteContext entityPersisteContext = EntityPersisteContext.create(metaModel, jdbcTemplate);
        return SimpleEntityManager.create(entityPersisteContext);
    }
}
