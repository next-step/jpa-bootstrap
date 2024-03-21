package jdbc;

import database.DatabaseServer;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcTemplatePool {

    private static JdbcTemplatePool instance;
    private static final List<JdbcTemplate> pool = new ArrayList<>();
    private final DatabaseServer databaseServer;

    private final int DEFAULT_POOL_SIZE = 10;

    private JdbcTemplatePool(DatabaseServer databaseServer) throws SQLException {
        this.databaseServer = databaseServer;
        for (int i = 0; i < DEFAULT_POOL_SIZE; i++) {
            pool.add(new JdbcTemplate(databaseServer.getConnection()));
        }
    }

    public static synchronized JdbcTemplatePool getInstance(DatabaseServer databaseServer) throws SQLException {
        if (instance == null) {
            instance = new JdbcTemplatePool(databaseServer);
        }
        return instance;
    }

    public JdbcTemplate getJdbcTemplate() throws SQLException {
        if (pool.isEmpty()) {
            throw new RuntimeException("사용가능 한 커넥션이 존재 하지 앖습니다.");
        }
        JdbcTemplate jdbcTemplate = pool.get(0);
        if (jdbcTemplate.isClosed()) {
            jdbcTemplate = new JdbcTemplate(databaseServer.getConnection());
        }
        return jdbcTemplate;
    }

    public static void releaseJdbcTemplate(JdbcTemplate jdbcTemplate) {
        pool.add(jdbcTemplate);
    }
}
