package util;

import database.H2ConnectionFactory;
import jdbc.JdbcTemplate;
import persistence.bootstrap.Metamodel;
import persistence.dialect.Dialect;
import persistence.dialect.H2Dialect;
import persistence.entity.manager.DefaultEntityManager;
import persistence.entity.manager.EntityManager;
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
        final Dialect dialect = new H2Dialect();
        final DmlQueries dmlQueries = new DmlQueries();
        final ProxyFactory proxyFactory = new ProxyFactory();
        return new Metamodel(jdbcTemplate, dialect, dmlQueries, proxyFactory, basePackages);
    }
}
