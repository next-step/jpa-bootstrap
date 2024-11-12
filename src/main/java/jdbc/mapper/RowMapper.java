package jdbc.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface RowMapper {
    Object mapRow(final ResultSet resultSet) throws SQLException, IllegalAccessException;
}