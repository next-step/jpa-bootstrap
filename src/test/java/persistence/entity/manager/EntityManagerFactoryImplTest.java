package persistence.entity.manager;

import database.DatabaseServer;
import database.H2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Nested
@DisplayName("EntityManagerFactoryImpl 클래스는")
class EntityManagerFactoryImplTest {
    @Nested
    @DisplayName("openSession 메소드는")
    class openSession {
        @Nested
        @DisplayName("데이터베이스 세션이 인자로 주어지면")
        class withDatabaseServer {
            @Test
            @DisplayName("새로운 엔티티매니저를 리턴한다")
            void returnEntityManager() throws SQLException {
                //given
                //when
                DatabaseServer databaseServer = new H2();
                EntityManagerFactory entityManagerFactory = new EntityManagerFactoryImpl(databaseServer);
                EntityManager entityManager = entityManagerFactory.openSession(databaseServer);
                //then
                assertThat(entityManager).isNotNull();
            }
        }
    }
}
