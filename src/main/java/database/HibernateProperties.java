package database;

import database.DataSourceProperties;
import persistence.sql.dialect.Dialect;

import java.sql.Connection;

public class HibernateProperties {

    private final Dialect dialect;
    private final DataSourceProperties dataSourceProperties;
    private final Connection connection;

    public HibernateProperties(Dialect dialect, DataSourceProperties dataSourceProperties, Connection connection) {
        this.dialect = dialect;
        this.dataSourceProperties = dataSourceProperties;
        this.connection = connection;
    }

    public Dialect getDialect() {
        return dialect;
    }

    public Connection getConnection() {
        return connection;
    }
}
