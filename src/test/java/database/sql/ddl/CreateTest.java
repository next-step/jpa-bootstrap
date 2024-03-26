package database.sql.ddl;

import database.dialect.Dialect;
import database.dialect.MySQLDialect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import persistence.bootstrap.Metadata;

import static org.assertj.core.api.Assertions.assertThat;
import static testsupport.EntityTestUtils.initializer;

class CreateTest {
    private final Dialect dialect = MySQLDialect.getInstance();
    private Metadata metadata;

    @BeforeEach
    void setUpMetadata() {
        metadata = initializer(null).getMetadata();
    }

    @ParameterizedTest
    @CsvSource(value = {
            "app.entity.OldPerson1:CREATE TABLE OldPerson1 (id BIGINT PRIMARY KEY, name VARCHAR(255) NULL, age INT NULL)",
            "app.entity.OldPerson2:CREATE TABLE OldPerson2 (id BIGINT AUTO_INCREMENT PRIMARY KEY, nick_name VARCHAR(255) NULL, old INT NULL, email VARCHAR(255) NOT NULL)",
            "app.entity.OldPerson3:CREATE TABLE users (id BIGINT AUTO_INCREMENT PRIMARY KEY, nick_name VARCHAR(255) NULL, old INT NULL, email VARCHAR(255) NOT NULL)",
            "app.entity.TestDepartment:CREATE TABLE department (id BIGINT PRIMARY KEY)",
            "app.entity.TestEmployee:CREATE TABLE employee (id BIGINT PRIMARY KEY, name VARCHAR(255) NULL, departure_id BIGINT NOT NULL)",
            "app.entity.Person5:CREATE TABLE users (id BIGINT AUTO_INCREMENT PRIMARY KEY, nick_name VARCHAR(255) NULL, old INT NULL, email VARCHAR(255) NOT NULL)"
    }, delimiter = ':')
    void buildCreateQuery(Class<?> clazz, String expected) {
        assertCreateQuery(clazz, expected);
    }

    private <T> void assertCreateQuery(Class<T> clazz, String expected) {
        Create create = Create.from(metadata.getPersistentClass(clazz), metadata, dialect);
        String actual = create.toSql();
        assertThat(actual).isEqualTo(expected);
    }
}
