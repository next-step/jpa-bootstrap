package persistence.session;

import database.DatabaseServer;
import database.H2;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.meta.Metadata;
import persistence.meta.MetadataImpl;
import persistence.meta.Metamodel;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

public class EntityManagerFactoryTest {

    private static DatabaseServer server;
    private static Metadata metadata;

    @BeforeEach
    void setUp() throws SQLException {
        server = new H2();
        server.start();
        metadata = new MetadataImpl(server);
    }

    @AfterEach
    void tearDown() {
        server.stop();
    }

    @Test
    @DisplayName("openSession이 호출될 때마다 새로운 세션을 생성한다.")
    void currentSessionExists() throws SQLException {
        EntityManagerFactory entityManagerFactory = new EntityManagerFactoryImpl(
                new ThreadLocalCurrentSessionContext(),
                metadata
        );

        EntityManager em1 = entityManagerFactory.openSession();
        EntityManager em2 = entityManagerFactory.openSession();

        assertThat(em1).isNotEqualTo(em2);
    }
}
