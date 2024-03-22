package database.sql.ddl;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import persistence.entity.context.PersistentClass;

import static org.assertj.core.api.Assertions.assertThat;

class DropTest {

    @ParameterizedTest
    @CsvSource(value = {
            "entity.OldPerson1:DROP TABLE OldPerson1",
            "entity.OldPerson2:DROP TABLE OldPerson2",
            "entity.OldPerson3:DROP TABLE users"
    }, delimiter = ':')
    void buildDeleteQuery(Class<?> clazz, String expected) {
        String actual = Drop.from(PersistentClass.from(clazz)).buildQuery();

        assertThat(actual).isEqualTo(expected);
    }
}
