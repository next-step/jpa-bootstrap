package database.sql.dml;

import app.entity.NoAutoIncrementUser;
import app.entity.Person4;
import database.sql.dml.part.ValueMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import persistence.bootstrap.Metadata;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static testsupport.EntityTestUtils.initializer;

class InsertTest {
    private Metadata metadata;

    @BeforeEach
    void setUp() {
        metadata = initializer(null).getMetadata();
    }

    static List<Arguments> testCases() {
        return List.of(
                arguments(ValueMap.of("nick_name", "abc"), "INSERT INTO users (nick_name) VALUES ('abc')"),
                arguments(ValueMap.from(Map.of("nick_name", "abc", "old", 14, "email", "a@b.com")),
                          "INSERT INTO users (nick_name, old, email) VALUES ('abc', 14, 'a@b.com')"),
                arguments(ValueMap.of("nick_name", "abc", "old", 14),
                          "INSERT INTO users (nick_name, old) VALUES ('abc', 14)"),
                arguments(ValueMap.of("nick_name", null, "old", 14),
                          "INSERT INTO users (nick_name, old) VALUES (NULL, 14)")
        );
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void buildInsertQuery(ValueMap valueMap, String expected) {
        String actual = Insert.from(metadata.getPersistentClass(Person4.class)).toSql(null, valueMap);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void insertQueryWithId() {
        ValueMap valueMap = ValueMap.from(Map.of("nick_name", "abc", "old", 14, "email", "a@b.com"));
        String actual = Insert.from(metadata.getPersistentClass(Person4.class)).toSql(10L, valueMap);
        assertThat(actual).isEqualTo("INSERT INTO users (id, nick_name, old, email) VALUES (10, 'abc', 14, 'a@b.com')");
    }

    @Test
    void insertIntoEntityWithNoId() {
        ValueMap valueMap = ValueMap.from(Map.of("nick_name", "abc", "old", 14, "email", "a@b.com"));
        String actual = Insert.from(metadata.getPersistentClass(NoAutoIncrementUser.class))
                .toSql(10L, valueMap);
        assertThat(actual)
                .isEqualTo("INSERT INTO users_no_auto_increment (id, nick_name, old, email) VALUES (10, 'abc', 14, 'a@b.com')");
    }
}
