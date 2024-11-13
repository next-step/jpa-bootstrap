package util;

import database.H2ConnectionFactory;
import jdbc.JdbcTemplate;
import persistence.bootstrap.Metamodel;
import persistence.dialect.Dialect;
import persistence.dialect.H2Dialect;
import persistence.entity.proxy.ProxyFactory;

public class TestHelper {
    private TestHelper() {
        throw new AssertionError();
    }

    public static Metamodel createMetamodel(String... basePackages) {
        final JdbcTemplate jdbcTemplate = new JdbcTemplate(H2ConnectionFactory.getConnection());
        final Dialect dialect = new H2Dialect();
        final ProxyFactory proxyFactory = ProxyFactory.getInstance();
        return new Metamodel(jdbcTemplate, dialect, proxyFactory, basePackages);
    }
}
