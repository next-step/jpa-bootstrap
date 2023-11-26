package persistence.entity;


import java.sql.Connection;
import persistence.dialect.Dialect;
import persistence.meta.MetaModel;
import persistence.sql.QueryGenerator;


public class EntityManagerFactory {
    private final MetaModel metaModel;
    private final QueryGenerator queryGenerator;

    private EntityManagerFactory(MetaModel metaModel, QueryGenerator queryGenerator) {
        this.metaModel = metaModel;
        this.queryGenerator = queryGenerator;
    }

    public static EntityManagerFactory create(MetaModel metaModel, Dialect dialect) {
        return new EntityManagerFactory(metaModel, QueryGenerator.of(dialect));
    }

    public EntityManager createEntityManager(Connection connection) {
        return new SimpleEntityManager(metaModel, connection, queryGenerator);
    }
}
