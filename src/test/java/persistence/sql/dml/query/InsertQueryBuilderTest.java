package persistence.sql.dml.query;

import domain.Person;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.definition.TableDefinition;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class InsertQueryBuilderTest {

    @Test
    @DisplayName("Should build insert users query")
    void shouldBuildInsertUsersQuery() {
        Person person = new Person(1L, "john_doe", 30, "chanho0912@gmail.com", 1);

        String query = new InsertQueryBuilder().build(person, new TableDefinition(Person.class));

        assertThat(query).isEqualTo("INSERT INTO users (id, nick_name, old, email) VALUES (1, 'john_doe', 30, 'chanho0912@gmail.com');");
    }

    @Entity
    private static class HasNullableColumnEntity1 {
        @Id
        private Long id;

        private String name;

        private Integer age;

        public HasNullableColumnEntity1() {
        }

        public HasNullableColumnEntity1(Long id) {
            this.id = id;
        }

        public HasNullableColumnEntity1(Long id, Integer age) {
            this.id = id;
            this.age = age;
        }
    }

    @Test
    void testIgnoreNullableColumnInInsertQuery() {
        HasNullableColumnEntity1 hasNullableColumnEntity1 = new HasNullableColumnEntity1(1L);
        HasNullableColumnEntity1 hasNullableColumnEntity2 = new HasNullableColumnEntity1(2L, 10);

        String query1 = new InsertQueryBuilder().build(hasNullableColumnEntity1, new TableDefinition(HasNullableColumnEntity1.class));
        String query2 = new InsertQueryBuilder().build(hasNullableColumnEntity2, new TableDefinition(HasNullableColumnEntity1.class));

        assertAll(
                () -> assertThat(query1).isEqualTo("INSERT INTO HasNullableColumnEntity1 (id) VALUES (1);"),
                () -> assertThat(query2).isEqualTo("INSERT INTO HasNullableColumnEntity1 (id, age) VALUES (2, 10);")
        );
    }
}
