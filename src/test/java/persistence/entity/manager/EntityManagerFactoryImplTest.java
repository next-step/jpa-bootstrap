package persistence.entity.manager;

import database.DatabaseServer;
import database.H2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@Nested
@DisplayName("EntityManagerFactoryImpl 클래스는")
class EntityManagerFactoryImplTest {
    @Nested
    @DisplayName("openSession 메소드는")
    class openSession {
        @Nested
        @DisplayName("DatabaseServer가 인자로 주어지면")
        class withDatabaseServer {
            @Test
            @DisplayName("세션을 연다")
            void returnEntityManager() throws SQLException {
                //given
                //when
                DatabaseServer databaseServer = new H2();
                EntityManagerFactory entityManagerFactory = new EntityManagerFactoryImpl(databaseServer);

                //then
                assertDoesNotThrow(entityManagerFactory::openSession);
            }
        }
    }
}
