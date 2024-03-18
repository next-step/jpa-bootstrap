package persistence.support;

import database.DatabaseServer;

import java.sql.Connection;

public class FakeDatabaseServer implements DatabaseServer {
    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public Connection getConnection() {
        return null;
    }
}
