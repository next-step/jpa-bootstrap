package persistence.entity.manager;

import entity.SampleOneWithValidAnnotation;
import entity.SampleTwoWithValidAnnotation;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import persistence.DatabaseTest;
import persistence.context.PersistenceContext;
import persistence.context.PersistenceContextImpl;
import persistence.entity.attribute.EntityAttribute;
import persistence.entity.attribute.EntityAttributes;
import persistence.entity.loader.EntityLoader;
import persistence.entity.loader.SimpleEntityLoader;
import persistence.entity.persister.SimpleEntityPersister;
import persistence.sql.dml.builder.InsertQueryBuilder;
import persistence.sql.infra.H2SqlConverter;

import java.sql.SQLException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Nested
@DisplayName("EntityManager 클래스의")
public class EntityManagerTest extends DatabaseTest {
    private final EntityAttributes entityAttributes = new EntityAttributes();

    @Nested
    @DisplayName("findById 메소드는")
    public class findById {

        @Nested
        @DisplayName("SampleOneWithValidAnnotation 클래스와 아이디가 주어졌을떄")
        public class withSampleOneWithValidAnnotation {
            @Test
            @DisplayName("적절한 SampleOneWithValidAnnotation 객체를 반환한다.")
            void returnObject() throws SQLException {
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

                EntityLoader entityLoader = new SimpleEntityLoader(jdbcTemplate);
                SimpleEntityPersister simpleEntityPersister = new SimpleEntityPersister(jdbcTemplate, entityLoader, entityAttributes);
                PersistenceContext persistenceContext = new PersistenceContextImpl(simpleEntityPersister, entityAttributes);
                EntityManagerImpl entityManager = EntityManagerImpl.of(persistenceContext);

                //when
                SampleOneWithValidAnnotation retrieved =
                        entityManager.findById(SampleOneWithValidAnnotation.class, "1");

                //then
                assertThat(retrieved.toString()).isEqualTo("SampleOneWithValidAnnotation{id=1, name='민준', age=29}");
            }
        }

        @Nested
        @DisplayName("SampleTwoWithValidAnnotation 클래스와 아이디가 주어졌을떄")
        public class withSampleTwoWithValidAnnotation {
            @Test
            @DisplayName("적절한 SampleTwoWithValidAnnotation 객체를 반환한다.")
            void returnObject() {
                //given
                setUpFixtureTable(SampleTwoWithValidAnnotation.class, new H2SqlConverter());

                EntityAttributes entityAttributes = new EntityAttributes();
                EntityAttribute entityAttribute =
                        entityAttributes.findEntityAttribute(SampleTwoWithValidAnnotation.class);

                SampleTwoWithValidAnnotation sample =
                        new SampleTwoWithValidAnnotation(1L, "민준", 29L);

                String insertDML
                        = new InsertQueryBuilder().prepareStatement(entityAttribute, sample);
                jdbcTemplate.execute(insertDML);

                EntityLoader entityLoader = new SimpleEntityLoader(jdbcTemplate);
                SimpleEntityPersister simpleEntityPersister = new SimpleEntityPersister(jdbcTemplate, entityLoader, entityAttributes);

                PersistenceContext persistenceContext = new PersistenceContextImpl(simpleEntityPersister, entityAttributes);
                EntityManagerImpl entityManager = EntityManagerImpl.of(persistenceContext);

                //when
                SampleTwoWithValidAnnotation retrieved =
                        entityManager.findById(SampleTwoWithValidAnnotation.class, "1");

                //then
                assertThat(retrieved.toString()).isEqualTo("SampleTwoWithValidAnnotation{id=1, name='민준', age=29}");
            }
        }
    }

    @Nested
    @DisplayName("persist 메소드는")
    public class persist {
        @Nested
        @DisplayName("SampleOneWithValidAnnotation 인스턴스가 주어졌을떄")
        public class withSampleOneWithValidAnnotation {
            @Test
            @DisplayName("아이디가 매핑된 객체를 반환한다.")
            void returnObject() {
                //given
                SampleOneWithValidAnnotation sample =
                        new SampleOneWithValidAnnotation("민준", 29);

                setUpFixtureTable(SampleOneWithValidAnnotation.class, new H2SqlConverter());

                EntityLoader entityLoader = new SimpleEntityLoader(jdbcTemplate);
                SimpleEntityPersister simpleEntityPersister = new SimpleEntityPersister(jdbcTemplate, entityLoader, entityAttributes);
                EntityAttributes entityAttributes = new EntityAttributes();
                PersistenceContext persistenceContext = new PersistenceContextImpl(simpleEntityPersister, entityAttributes);
                EntityManagerImpl entityManager = EntityManagerImpl.of(persistenceContext);

                //when
                SampleOneWithValidAnnotation persisted = entityManager.persist(sample);

                //then
                assertThat(persisted.toString())
                        .isEqualTo("SampleOneWithValidAnnotation{id=1, name='민준', age=29}");
            }
        }
    }

    @Nested
    @DisplayName("remove 메소드는")
    public class remove {
        @Nested
        @DisplayName("디비에 저장된 인스턴스가 주어졌을떄")
        public class withSampleOneWithValidAnnotation {
            @Test
            @DisplayName("객체를 제거한다.")
            void notThrow() {
                //given
                SampleOneWithValidAnnotation sample =
                        new SampleOneWithValidAnnotation("민준", 29);

                setUpFixtureTable(SampleOneWithValidAnnotation.class, new H2SqlConverter());

                EntityLoader entityLoader = new SimpleEntityLoader(jdbcTemplate);
                SimpleEntityPersister simpleEntityPersister = new SimpleEntityPersister(jdbcTemplate, entityLoader, entityAttributes);
                EntityAttributes entityAttributes = new EntityAttributes();
                PersistenceContext persistenceContext = new PersistenceContextImpl(simpleEntityPersister, entityAttributes);
                EntityManagerImpl entityManager = EntityManagerImpl.of(persistenceContext);

                //when
                SampleOneWithValidAnnotation inserted = entityManager.persist(sample);

                //then
                Assertions.assertDoesNotThrow(() -> entityManager.remove(inserted));
            }
        }
    }
}
