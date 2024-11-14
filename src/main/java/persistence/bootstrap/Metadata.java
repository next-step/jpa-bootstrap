package persistence.bootstrap;

import jdbc.JdbcTemplate;
import persistence.dialect.Dialect;
import persistence.entity.manager.CurrentSessionContext;
import persistence.entity.manager.factory.EntityManagerFactory;
import persistence.entity.proxy.ProxyFactory;

import java.sql.Connection;

public class Metadata {
    private final Metamodel metamodel;

    public Metadata(Connection connection, Dialect dialect, String... basePackages) {
        final JdbcTemplate jdbcTemplate = new JdbcTemplate(connection);
        final ProxyFactory proxyFactory = ProxyFactory.getInstance();
        this.metamodel = new Metamodel(jdbcTemplate, dialect, proxyFactory, basePackages);
    }

    public EntityManagerFactory getEntityManagerFactory() {
        final CurrentSessionContext currentSessionContext = new CurrentSessionContext();
        return new EntityManagerFactory(currentSessionContext, metamodel);
    }
}
