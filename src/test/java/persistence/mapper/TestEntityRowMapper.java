package persistence.mapper;

import entity.SampleOneWithValidAnnotation;
import jdbc.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TestEntityRowMapper implements RowMapper<List<SampleOneWithValidAnnotation>> {
    @Override
    public List<SampleOneWithValidAnnotation> mapRow(ResultSet rs) throws SQLException {
        List<SampleOneWithValidAnnotation> entities = new ArrayList<>();

        while (rs.next()) {
            entities.add(new SampleOneWithValidAnnotation(
                    rs.getLong("ID"),
                    rs.getString("NAME"),
                    rs.getInt("OLD")
            ));
        }

        return entities;
    }
}
