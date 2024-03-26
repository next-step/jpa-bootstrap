package database.sql.ddl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import persistence.bootstrap.Metadata;

import static org.assertj.core.api.Assertions.assertThat;
import static testsupport.EntityTestUtils.initializer;

class DropTest {
    private Metadata metadata;

    @BeforeEach
    void setUpMetadata() {
        metadata = initializer(null).getMetadata();
    }

    @ParameterizedTest
    @CsvSource(value = {
            "app.entity.OldPerson1:DROP TABLE OldPerson1",
            "app.entity.OldPerson2:DROP TABLE OldPerson2",
            "app.entity.OldPerson3:DROP TABLE users"
    }, delimiter = ':')
    void buildDeleteQuery(Class<?> clazz, String expected) {
        String actual = Drop.from(metadata.getPersistentClass(clazz)).toSql(false);
        assertThat(actual).isEqualTo(expected);
    }
}
