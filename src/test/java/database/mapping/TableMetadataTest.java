package database.mapping;

import app.entity.OldPerson1;
import app.entity.Person4;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TableMetadataTest {
    @Test
    void getTableName() {
        TableMetadata tableMetadata = new TableMetadata(Person4.class);
        String tableName = tableMetadata.getTableName();
        assertThat(tableName).isEqualTo("users");
    }

    @Test
    void getTableNameWithoutTableAnnotation() {
        TableMetadata tableMetadata = new TableMetadata(OldPerson1.class);
        String tableName = tableMetadata.getTableName();
        assertThat(tableName).isEqualTo("OldPerson1");
    }
}
