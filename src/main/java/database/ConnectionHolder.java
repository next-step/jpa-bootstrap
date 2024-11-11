package database;

import persistence.sql.dml.Database;

import java.sql.Connection;

public final class ConnectionHolder {
    private static final ThreadLocal<Connection> connectionHolder = new ThreadLocal<>();

    private static Database database;

    public ConnectionHolder(Database database) {
        ConnectionHolder.database = database;
    }

    public static void updateDatabase(Database database) {
        ConnectionHolder.database = database;
    }

    public static Connection getConnection() {
        Connection connection = connectionHolder.get();
        if (connection == null) {
            connection = database.getConnection();
            connectionHolder.set(connection);
        }
        return connection;
    }
}
