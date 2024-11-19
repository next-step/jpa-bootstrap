package persistence;

import boot.Metamodel;
import boot.MetamodelImpl;
import builder.ddl.DDLBuilderData;
import builder.ddl.builder.CreateQueryBuilder;
import builder.ddl.builder.DropQueryBuilder;
import builder.ddl.dataType.DB;
import builder.dml.EntityData;
import builder.dml.builder.DMLQueryBuilder;
import database.H2DBConnection;
import entity.Person;
import event.EventListenerRegistry;
import event.action.ActionQueue;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/*
- Persist로 Person 저장 후 영속성 컨텍스트에 존재하는지 확인한다.
- remove 실행하면 영속성 컨텍스트에 데이터가 제거된다.
- update 실행하면 영속성컨텍스트 데이터도 수정된다.
- update 실행하면 snapShot 데이터도 수정된다.
- flush를 하지 않으면 데이터가 반영되지 않는다.
*/
class EntityManagerTest {

    private EntityManager entityManager;
    private H2DBConnection h2DBConnection;
    private JdbcTemplate jdbcTemplate;
    private PersistenceContext persistenceContext;

    @BeforeEach
    void setUp() {
        this.h2DBConnection = new H2DBConnection();
        this.jdbcTemplate = this.h2DBConnection.start();

        //테이블 생성
        CreateQueryBuilder queryBuilder = new CreateQueryBuilder();
        String createQuery = queryBuilder.buildQuery(DDLBuilderData.createDDLBuilderData(Person.class, DB.H2));

        jdbcTemplate.execute(createQuery);

        this.persistenceContext = new PersistenceContextImpl();

        Metamodel metamodel = new MetamodelImpl(jdbcTemplate);
        metamodel.init();

        ActionQueue actionQueue = new ActionQueue();

        this.entityManager = new EntityManagerImpl(persistenceContext, metamodel, new EventListenerRegistry(metamodel, new EntityLoader(jdbcTemplate, new DMLQueryBuilder()), actionQueue),actionQueue);
    }

    //정확한 테스트를 위해 메소드마다 테이블 DROP 후 DB종료
    @AfterEach
    void tearDown() {
        DropQueryBuilder queryBuilder = new DropQueryBuilder();
        String dropQuery = queryBuilder.buildQuery(DDLBuilderData.createDDLBuilderData(Person.class, DB.H2));
        jdbcTemplate.execute(dropQuery);
        this.h2DBConnection.stop();
    }

    @DisplayName("Persist로 Person 저장 후 영속성 컨텍스트에 존재하는지 확인한다.")
    @Test
    void findTest() {
        Person person = createPerson(1);
        this.entityManager.persist(person);
        this.entityManager.flush();
        EntityData EntityData = this.persistenceContext.findEntity(new EntityKey(person.getId(), person.getClass()));
        assertThat(EntityData.getEntityInstance())
                .extracting("id", "name", "age", "email")
                .contains(1L, "test1", 29, "test@test.com");
    }

    @DisplayName("remove 실행하면 영속성 컨텍스트에 데이터가 제거된다.")
    @Test
    void removeTest() {
        Person person = createPerson(1);
        this.entityManager.persist(person);
        this.entityManager.remove(person);
        this.entityManager.flush();
        assertThat(this.persistenceContext.findEntity(new EntityKey(person.getId(), person.getClass()))).isNull();
    }

    @DisplayName("update 실행하면 영속성컨텍스트 데이터도 수정된다.")
    @Test
    void updateTest() {
        Person person = createPerson(1);
        this.entityManager.persist(person);

        person.changeEmail("changed@test.com");
        this.entityManager.merge(person);
        this.entityManager.flush();
        EntityData EntityData = this.persistenceContext.findEntity(new EntityKey(person.getId(), person.getClass()));

        assertThat(EntityData.getEntityInstance())
                .extracting("id", "name", "age", "email")
                .contains(1L, "test1", 29, "changed@test.com");
    }

    @DisplayName("update 실행하면 snapShot 데이터도 수정된다.")
    @Test
    void updateSnapShotTest() {
        Person person = createPerson(1);
        this.entityManager.persist(person);

        person.changeEmail("changed@test.com");
        this.entityManager.merge(person);

        this.entityManager.flush();

        EntityData EntityData = this.persistenceContext.getDatabaseSnapshot(new EntityKey(person.getId(), person.getClass()));

        assertThat(EntityData.getEntityInstance())
                .extracting("id", "name", "age", "email")
                .contains(1L, "test1", 29, "changed@test.com");
    }

    @DisplayName("영속성 컨텍스트 Clear하여 데이터가 반영됐는지 확인한다.")
    @Test
    void flushTest() {
        Person person = createPerson(1);
        this.entityManager.persist(person);

        this.entityManager.clear();

        Person findPerson = this.entityManager.find(Person.class, 1L);
        assertThat(findPerson)
                .extracting("id", "name", "age", "email")
                .contains(1L, "test1", 29, "test@test.com");
    }

    @DisplayName("영속성 컨텍스트를 clear하여 데이터가 수정됐는지 확인한다.")
    @Test
    void mergeFlushTest() {
        Person person = createPerson(1);
        this.entityManager.persist(person);

        person.changeEmail("changed@test.com");
        this.entityManager.merge(person);

        this.entityManager.clear();

        Person findPerson = this.entityManager.find(Person.class, 1L);
        assertThat(findPerson)
                .extracting("id", "name", "age", "email")
                .contains(1L, "test1", 29, "changed@test.com");
    }

    private Person createPerson(int i) {
        return new Person((long) i, "test" + i, 29, "test@test.com");
    }
}
