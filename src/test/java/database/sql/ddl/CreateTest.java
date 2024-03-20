package database.sql.ddl;

import database.dialect.Dialect;
import database.dialect.MySQLDialect;
import database.mapping.AllEntities;
import entity.Departure;
import entity.Employee;
import entity.Person;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class CreateTest {
    private final Dialect dialect = MySQLDialect.getInstance();

    @BeforeAll
    static void setUpEntities() {
        AllEntities.register(Departure.class);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "entity.OldPerson1:CREATE TABLE OldPerson1 (id BIGINT PRIMARY KEY, name VARCHAR(255) NULL, age INT NULL)",
            "entity.OldPerson2:CREATE TABLE OldPerson2 (id BIGINT AUTO_INCREMENT PRIMARY KEY, nick_name VARCHAR(255) NULL, old INT NULL, email VARCHAR(255) NOT NULL)",
            "entity.OldPerson3:CREATE TABLE users (id BIGINT AUTO_INCREMENT PRIMARY KEY, nick_name VARCHAR(255) NULL, old INT NULL, email VARCHAR(255) NOT NULL)"
    }, delimiter = ':')
    void buildCreateQuery(Class<?> clazz, String expected) {
        assertCreateQuery(clazz, expected);
    }

    @Test
    void buildCreateQueryForAssociatedEntity() {
        assertCreateQuery(Departure.class, "CREATE TABLE Departure (id BIGINT PRIMARY KEY)");
        assertCreateQuery(Employee.class, "CREATE TABLE Employee (id BIGINT PRIMARY KEY, name VARCHAR(255) NULL, departure_id BIGINT NOT NULL)");
    }

    private void assertCreateQuery(Class<?> clazz, String expected) {
        Create create = new Create(clazz, dialect);
        String actual = create.buildQuery();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getColumnDefinitions() {
        assertCreateQuery(
                Person.class,
                "CREATE TABLE users (id BIGINT AUTO_INCREMENT PRIMARY KEY, nick_name VARCHAR(255) NULL, old INT NULL, email VARCHAR(255) NOT NULL)"
        );
    }
}
