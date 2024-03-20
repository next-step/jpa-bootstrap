package database;

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

    public DataSourceProperties getDataSourceProperties() {
        return dataSourceProperties;
    }

    public Connection getConnection() {
        return connection;
    }
}
