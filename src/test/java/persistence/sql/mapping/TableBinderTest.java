package persistence.sql.mapping;

import jakarta.persistence.Table;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.model.PersistentClassMapping;
import persistence.sql.ddl.PersonV3;

import static org.assertj.core.api.Assertions.assertThat;

class TableBinderTest {

    private final TableBinder tableBinder = new TableBinder();
    private final static PersistentClassMapping persistentClassMapping = new PersistentClassMapping();

    @BeforeAll
    static void beforeAll() {
        persistentClassMapping.putPersistentClass(PersonV3.class);
    }

    @DisplayName("Entity 의 class 를 이용해 Table 객체를 생성한다")
    @Test
    public void createTableByEntity() throws Exception {
        // given
        final Class<PersonV3> clazz = PersonV3.class;
        final String tableName = clazz.getAnnotation(Table.class).name();

        // when
        final persistence.sql.mapping.Table result = tableBinder.createTable(persistentClassMapping.getPersistentClass(clazz));

        // then
        assertThat(result.getName()).isEqualTo(tableName);
    }

}
