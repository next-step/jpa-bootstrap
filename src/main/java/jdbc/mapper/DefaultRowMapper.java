package jdbc.mapper;

import jdbc.mapping.AssociationFieldMapping;
import jdbc.mapping.FieldMapping;
import jdbc.mapping.SimpleFieldMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.meta.EntityTable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DefaultRowMapper implements RowMapper {
    private static final Logger logger = LoggerFactory.getLogger(DefaultRowMapper.class);

    private static final String NOT_SUPPORTS_MAPPING_MESSAGE = "지원하는 FieldMapping이 존재하지 않습니다.";

    private final List<FieldMapping> fieldMappings = new ArrayList<>();
    private final EntityTable entityTable;

    public DefaultRowMapper(EntityTable entityTable) {
        this.entityTable = entityTable;
        initMappings();
    }

    public DefaultRowMapper(EntityTable entityTable, EntityTable childEntityTable) {
        this.entityTable = entityTable;
        initMappings(childEntityTable);
    }

    @Override
    public Object mapRow(ResultSet resultSet) throws SQLException, IllegalAccessException {
        final FieldMapping fieldMapping = getMapping();
        return fieldMapping.getRow(resultSet, entityTable);
    }

    private FieldMapping getMapping() {
        return fieldMappings.stream()
                .filter(fieldMapping -> fieldMapping.supports(entityTable))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(NOT_SUPPORTS_MAPPING_MESSAGE));
    }

    private void initMappings() {
        fieldMappings.add(new SimpleFieldMapping());
    }

    private void initMappings(EntityTable childEntityTable) {
        fieldMappings.add(new SimpleFieldMapping());
        fieldMappings.add(new AssociationFieldMapping(childEntityTable));
    }
}
