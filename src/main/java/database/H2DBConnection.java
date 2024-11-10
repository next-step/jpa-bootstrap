package database;

import jdbc.JdbcTemplate;

import java.sql.SQLException;

public class H2DBConnection {
    private final DatabaseServer server;

    public H2DBConnection() {
        try {
            this.server = new H2();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public JdbcTemplate start() {
        try {
            server.start();
            return new JdbcTemplate(server.getConnection());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void stop() {
        server.stop();
    }
}
