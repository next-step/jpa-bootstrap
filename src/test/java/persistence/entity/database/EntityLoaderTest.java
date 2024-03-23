package persistence.entity.database;

import app.entity.Person5;
import database.dialect.Dialect;
import database.dialect.MySQLDialect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.bootstrap.EntityManagerFactory;
import persistence.bootstrap.Initializer;
import persistence.entitymanager.SessionContract;
import testsupport.H2DatabaseTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static testsupport.EntityTestUtils.assertSamePerson;

class EntityLoaderTest extends H2DatabaseTest {
    private final Dialect dialect = MySQLDialect.getInstance();

    private SessionContract sessionContract;

    @BeforeEach
    void setUp() {
        Initializer initializer = new Initializer("app.entity", jdbcTemplate, dialect);
        initializer.initialize();
        EntityManagerFactory entityManagerFactory = initializer.createEntityManagerFactory();

        sessionContract = (SessionContract) entityManagerFactory.openSession();
    }

    @Test
    void loadMissingRecord() {
        Optional<Person5> entity = sessionContract.getEntityLoader(Person5.class).load(1L);
        assertThat(entity).isEmpty();
    }

    @Test
    void load2() {
        Person5 person = new Person5(null, "abc123", 14, "c123@d.com");
        sessionContract.getEntityPersister(Person5.class).insert(person);

        Optional<Person5> found = sessionContract.getEntityLoader(Person5.class).load(1L);

        assertSamePerson(found.get(), person, false);
    }
}
