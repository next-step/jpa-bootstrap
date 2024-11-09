package persistence.session;

import database.DatabaseServer;
import database.H2;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.meta.Metamodel;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class EntityManagerFactoryTest {

    private static DatabaseServer server;
    private static Metamodel metamodel;
    private static JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() throws SQLException {
        server = new H2();
        server.start();
    }

    @AfterEach
    void tearDown() {
        server.stop();
    }

    @Test
    @DisplayName("현재 session이 존재하면 새롭게 session을 open할 수 없다.")
    void currentSessionExists() throws SQLException {
        EntityManagerFactory entityManagerFactory = new EntityManagerFactoryImpl(
                new ThreadLocalCurrentSessionContext(),
                server
        );

        entityManagerFactory.openSession();
        assertThrows(IllegalStateException.class, entityManagerFactory::openSession);

    }
}
