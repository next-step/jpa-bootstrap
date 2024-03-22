package jdbc;

import database.DatabaseServer;
import java.sql.ResultSet;
import java.sql.Statement;
import static java.sql.Statement.RETURN_GENERATED_KEYS;
import java.util.ArrayList;
import java.util.List;

public class JdbcTemplate {
    private final DatabaseServer databaseServer;

    public JdbcTemplate(final DatabaseServer databaseServer) {
        this.databaseServer = databaseServer;
    }

    public void execute(final String sql) {
        System.out.println(sql);
        try (final Statement statement = databaseServer.getConnection().createStatement()) {
            statement.execute(sql);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Object executeInsert(final String sql) {
        System.out.println(sql);
        try (final Statement statement = databaseServer.getConnection().createStatement()) {
            statement.executeUpdate(sql, RETURN_GENERATED_KEYS);
            ResultSet generatedKeys = statement.getGeneratedKeys();
            generatedKeys.next();
            return generatedKeys.getObject(1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public int executeUpdate(final String sql) {
        System.out.println(sql);
        try (final Statement statement = databaseServer.getConnection().createStatement()) {
            return statement.executeUpdate(sql);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T queryForObject(final String sql, final RowMapper<T> rowMapper) {
        final List<T> results = query(sql, rowMapper);
        if (results.size() != 1) {
            throw new RuntimeException("Expected 1 result, got " + results.size());
        }
        return results.get(0);
    }

    public <T> List<T> query(final String sql, final RowMapper<T> rowMapper) {
        System.out.println(sql);
        try (final ResultSet resultSet = databaseServer.getConnection().prepareStatement(sql).executeQuery()) {
            final List<T> result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(rowMapper.mapRow(resultSet));
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
