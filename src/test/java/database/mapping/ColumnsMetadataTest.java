package database.mapping;

import app.entity.Person;
import database.mapping.column.EntityColumn;
import database.mapping.column.GeneralEntityColumn;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class ColumnsMetadataTest {
    private final ColumnsMetadata columnsMetadata = ColumnsMetadata.fromClass(Person.class);

    @Test
    void getPrimaryKeyColumnName() {
        String primaryKeyColumnName = columnsMetadata.getPrimaryKey().getColumnName();

        assertThat(primaryKeyColumnName).isEqualTo("id");
    }

    @Test
    void getGeneralColumns() {
        List<String> columnNames = columnsMetadata.getGeneralColumns().stream()
                .map(GeneralEntityColumn::getColumnName)
                .collect(Collectors.toList());

        assertThat(columnNames).containsExactly("nick_name", "old", "email");
    }

    @Test
    void getAllColumnNames() {
        List<String> allColumnNames = columnsMetadata.getAllEntityColumns().stream()
                .map(EntityColumn::getColumnName)
                .collect(Collectors.toList());

        assertThat(allColumnNames).containsExactly("id", "nick_name", "old", "email");
    }
}
