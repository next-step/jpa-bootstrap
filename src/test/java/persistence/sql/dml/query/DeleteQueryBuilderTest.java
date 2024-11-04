package persistence.sql.dml.query;

import domain.Person;
import org.junit.jupiter.api.Test;
import persistence.sql.definition.TableDefinition;

import static org.assertj.core.api.Assertions.assertThat;

class DeleteQueryBuilderTest {
    @Test
    void testDeleteById() {
        Person person = new Person(1L, "John", 30, "", 1);
        final String query = new DeleteQueryBuilder().build(person, new TableDefinition(Person.class));

        assertThat(query).isEqualTo("DELETE FROM users WHERE id = 1;");
    }
}
