package persistence.entity;

import database.DatabaseServer;
import database.H2;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EntityManagerFactoryTest {

    private DatabaseServer server;

    @BeforeEach
    void setUp() throws SQLException {
        server = new H2();
        server.start();
    }

    @AfterEach
    void tearDown() {
        CurrentSessionContext.close();
        server.stop();
    }

    @Test
    @DisplayName("세션 오픈 후 Entity Manager 정상반환")
    void openSession() throws Exception {
        EntityManagerFactory entityManagerFactory = EntityManagerFactoryImpl.of(server.getConnection());
        EntityManager entityManager = entityManagerFactory.openSession();
        assertThat(entityManager).isNotNull();
    }

    @Test
    @DisplayName("세션 중복오픈 시, 기 생성 세션오류 발생")
    void openDuplicateSession() throws Exception {
        EntityManagerFactory entityManagerFactory = EntityManagerFactoryImpl.of(server.getConnection());
        entityManagerFactory.openSession();
        assertThrows(IllegalStateException.class, entityManagerFactory::openSession, "세션 생성이 완료되었습니다.");
    }

}
