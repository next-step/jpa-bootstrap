package database.mapping.rowmapper;

import database.dialect.Dialect;
import jdbc.RowMapper;
import persistence.entity.context.PersistentClass;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

// TODO: 테스트 추가
public class JoinedRowMapper<T> implements RowMapper<JoinedRow<T>> {
    private final Dialect dialect;
    private final PersistentClass<T> persistentClass;

    public JoinedRowMapper(PersistentClass<T> persistentClass, Dialect dialect) {
        this.persistentClass = persistentClass;
        this.dialect = dialect;
    }

    @Override
    public JoinedRow<T> mapRow(ResultSet resultSet) throws SQLException {
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

        JoinedRow<T> joinedRow = new JoinedRow<>(persistentClass);
        for (int columnIndex = 1; columnIndex < resultSetMetaData.getColumnCount() + 1; columnIndex++) {
            joinedRow.add(getTableName(resultSetMetaData, columnIndex),
                          getColumnName(resultSetMetaData, columnIndex),
                          getValue(resultSetMetaData, resultSet, columnIndex));
        }
        return joinedRow;
    }

    private String getColumnName(ResultSetMetaData metaData, int columnIndex) throws SQLException {
        return metaData.getColumnName(columnIndex);
    }

    private String getTableName(ResultSetMetaData metaData, int columnIndex) throws SQLException {
        return metaData.getTableName(columnIndex);
    }

    private Object getValue(ResultSetMetaData metaData, ResultSet resultSet, int columnIndex) throws SQLException {
        return dialect.getFieldValueFromResultSet(resultSet, columnIndex, metaData.getColumnType(columnIndex));
    }
}
