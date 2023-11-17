package persistence.core;

import database.DatabaseServer;
import persistence.dialect.Dialect;
import persistence.exception.PersistenceException;
import persistence.sql.dml.DmlGenerator;

import java.sql.Connection;
import java.sql.SQLException;

public class PersistenceEnvironment {
    private final DatabaseServer server;
    private final Dialect dialect;
    private final DmlGenerator dmlGenerator;

    public PersistenceEnvironment(final DatabaseServer server, final Dialect dialect) {
        this.server = server;
        this.dialect = dialect;
        this.dmlGenerator = new DmlGenerator(dialect);
    }

    public Dialect getDialect() {
        return this.dialect;
    }

    public Connection getConnection() throws SQLException {
        return this.server.getConnection();
    }

    public DmlGenerator getDmlGenerator() {
        return this.dmlGenerator;
    }

}
