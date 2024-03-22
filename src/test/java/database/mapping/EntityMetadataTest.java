package database.mapping;

import database.mapping.column.EntityColumn;
import entity.Person4;
import org.junit.jupiter.api.Test;
import persistence.entity.context.PersistentClass;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class EntityMetadataTest {
    private final PersistentClass<Person4> persistentClass = PersistentClass.from(Person4.class);

    @Test
    void getTableName() {
        String tableName = persistentClass.getTableName();
        assertThat(tableName).isEqualTo("users");
    }

    @Test
    void getAllColumnNames() {
        List<String> allColumnNames = persistentClass.getAllEntityColumns().stream().map(EntityColumn::getColumnName)
                .collect(Collectors.toList());
        assertThat(allColumnNames).containsExactly("id", "nick_name", "old", "email");
    }
}
