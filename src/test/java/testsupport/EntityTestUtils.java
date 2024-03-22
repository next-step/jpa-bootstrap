package testsupport;

import database.sql.dml.Select;
import entity.Person5;
import jdbc.JdbcTemplate;
import persistence.entity.context.PersistentClass;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class EntityTestUtils {
    private EntityTestUtils() {
    }

    public static Person5 newPerson(Long id, String name, int age, String email) {
        return new Person5(id, name, age, email);
    }

    public static void assertSamePerson(Person5 actual, Person5 expected, boolean compareIdField) {
        assertAll(
                () -> {
                    if (compareIdField) assertThat(actual.getId()).isEqualTo(expected.getId());
                },
                () -> assertThat(actual.getName()).isEqualTo(expected.getName()),
                () -> assertThat(actual.getAge()).isEqualTo(expected.getAge()),
                () -> assertThat(actual.getEmail()).isEqualTo(expected.getEmail()));
    }

    public static List<Person5> findPeople(JdbcTemplate jdbcTemplate) {
        String query = Select.from(PersistentClass.from(Person5.class), List.of()).buildQuery();
        return jdbcTemplate.query(query, resultSet -> new Person5(
                resultSet.getLong("id"),
                resultSet.getString("nick_name"),
                resultSet.getInt("old"),
                resultSet.getString("email")));
    }

    public static Long getLastSavedId(JdbcTemplate jdbcTemplate1) {
        List<Person5> people = findPeople(jdbcTemplate1);
        if (people.isEmpty()) return null;
        return people.get(people.size() - 1).getId();
    }
}
