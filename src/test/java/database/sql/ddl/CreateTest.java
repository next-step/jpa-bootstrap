package database.sql.ddl;

import database.dialect.Dialect;
import database.dialect.MySQLDialect;
import entity.Person;
import entity.TestDepartment;
import entity.TestEmployee;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import persistence.bootstrap.MetadataImpl;
import persistence.entity.context.PersistentClass;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CreateTest {
    private final Dialect dialect = MySQLDialect.getInstance();

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
        assertCreateQuery(TestDepartment.class, "CREATE TABLE department (id BIGINT PRIMARY KEY)");
        assertCreateQuery(TestEmployee.class, "CREATE TABLE employee (id BIGINT PRIMARY KEY, name VARCHAR(255) NULL, departure_id BIGINT NOT NULL)");
    }

    private <T> void assertCreateQuery(Class<T> clazz, String expected) {
        MetadataImpl.INSTANCE.setComponents(List.of(TestDepartment.class));
        PersistentClass<T> persistentClass = MetadataImpl.INSTANCE.getPersistentClass(clazz);
        Create<T> create = Create.from(persistentClass, dialect);
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
