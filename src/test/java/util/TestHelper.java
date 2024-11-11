package util;

import database.H2ConnectionFactory;
import jdbc.JdbcTemplate;
import persistence.bootstrap.Metamodel;
import persistence.entity.DefaultEntityManager;
import persistence.entity.EntityManager;
import persistence.entity.proxy.ProxyFactory;
import persistence.sql.dml.DmlQueries;

public class TestHelper {
    private TestHelper() {
        throw new AssertionError();
    }

    public static EntityManager createEntityManager(String... basePackages) {
        final Metamodel metamodel = createMetamodel(basePackages);
        return new DefaultEntityManager(metamodel);
    }

    public static Metamodel createMetamodel(String... basePackages) {
        final JdbcTemplate jdbcTemplate = new JdbcTemplate(H2ConnectionFactory.getConnection());
        final DmlQueries dmlQueries = new DmlQueries();
        final ProxyFactory proxyFactory = new ProxyFactory();
        return new Metamodel(jdbcTemplate, dmlQueries, proxyFactory, basePackages);
    }
}
