package persistence.event;

import database.DatabaseServer;
import database.H2;
import entity.SampleOneWithValidAnnotation;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import persistence.DatabaseTest;
import persistence.entity.attribute.EntityAttribute;
import persistence.entity.attribute.EntityAttributes;
import persistence.entity.manager.EntityManager;
import persistence.entity.manager.EntityManagerFactory;
import persistence.entity.manager.EntityManagerFactoryImpl;
import persistence.listener.LoadEvent;
import persistence.listener.LoadEventListenerImpl;
import persistence.sql.dml.builder.InsertQueryBuilder;
import persistence.sql.infra.H2SqlConverter;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@Nested
@DisplayName("LoadEventListenerImpl 클래스는")
class LoadEventListenerImplTest extends DatabaseTest {

    @Nested
    @DisplayName("onLoad 메소드는")
    class onLoad {

        @Nested
        @DisplayName("LoadEvent와 LoadType이 주어지면")
        class withLoadEventAndLoadType {
            @Test
            @DisplayName("해당하는 엔티티를 Event에 저장한다.")
            public void setEntityAtLoadEvent() throws SQLException {
                //given
                setUpFixtureTable(SampleOneWithValidAnnotation.class, new H2SqlConverter());

                EntityAttributes entityAttributes = new EntityAttributes();
                EntityAttribute entityAttribute =
                        entityAttributes.findEntityAttribute(SampleOneWithValidAnnotation.class);
                JdbcTemplate jdbcTemplate = new JdbcTemplate(server.getConnection());
                SampleOneWithValidAnnotation sample =
                        new SampleOneWithValidAnnotation(1L, "민준", 29);

                String insertDML
                        = new InsertQueryBuilder().prepareStatement(entityAttribute, sample);
                jdbcTemplate.execute(insertDML);

                DatabaseServer databaseServer = new H2();
                EntityManagerFactory entityManagerFactory = new EntityManagerFactoryImpl(databaseServer);
                entityManagerFactory.openSession();
                EntityManager entityManager = entityManagerFactory.getSession();

                LoadEventListenerImpl loadEventListener = new LoadEventListenerImpl(entityManager);

                //when
                //then
                assertDoesNotThrow(() -> loadEventListener.onLoad(new LoadEvent(
                        entityAttribute.getClazz(), "1")));
            }
        }
    }
}
