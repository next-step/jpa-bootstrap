package database.sql.dml;

import app.entity.Person4;
import database.sql.dml.part.WhereMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.bootstrap.Metadata;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static testsupport.EntityTestUtils.initializer;

class SelectTest {
    private Select selectQuery;

    @BeforeEach
    void setUp() {
        Metadata metadata = initializer(null).getMetadata();
        selectQuery = Select.from(metadata.getPersistentClass(Person4.class), metadata);
    }

    @Test
    void buildSelectQuery() {
        String actual = selectQuery.toSql();
        assertThat(actual).isEqualTo("SELECT id, nick_name, old, email FROM users");
    }

    @Test
    void buildSelectQueryWithCollection() {
        String query = selectQuery.toSql(List.of(1L, 2L));
        assertThat(query).isEqualTo("SELECT id, nick_name, old, email FROM users WHERE id IN (1, 2)");

    }

    @Test
    void buildSelectQueryWithEmptyCollection() {
        String emptyArrayQuery = selectQuery.toSql(List.of());
        assertThat(emptyArrayQuery).isEqualTo("SELECT id, nick_name, old, email FROM users WHERE id IN ()");
    }

    @Test
    void buildSelectQueryWithInvalidColumn() {
        RuntimeException exception = assertThrows(RuntimeException.class,
                                                  () -> selectQuery.toSql(WhereMap.of("aaaaa", List.of())));
        assertThat(exception.getMessage()).isEqualTo("Invalid query: aaaaa");
    }

    @Test
    void buildSelectPrimaryKeyQuery() {
        String actual = selectQuery.toSql(1L);
        assertThat(actual).isEqualTo("SELECT id, nick_name, old, email FROM users WHERE id = 1");
    }
}
