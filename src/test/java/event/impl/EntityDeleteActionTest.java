package event.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.TestEntityInitialize;
import persistence.config.TestPersistenceConfig;
import persistence.sql.context.EntityPersister;
import persistence.sql.dml.EntityManager;
import persistence.sql.fixture.TestPerson;
import persistence.sql.loader.EntityLoader;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("EntityDeleteAction 클래스 테스트")
class EntityDeleteActionTest extends TestEntityInitialize {
    private EntityLoader<TestPerson> loader;
    private EntityPersister<TestPerson> persister;

    @BeforeEach
    void setUp() throws SQLException {
        TestPersistenceConfig config = TestPersistenceConfig.getInstance();

        EntityManager entityManager = config.entityManagerFactory().entityManager();

        persister = entityManager.getEntityPersister(TestPerson.class);
        loader = entityManager.getEntityLoader(TestPerson.class);
    }

    @DisplayName("execute 함수는 persister의 delete 함수를 호출한다.")
    @Test
    void execute() {
        // given
        TestPerson person = new TestPerson("catsbi", 33, "catsbi@naver.com", 123);
        persister.insert(person);


        // when
        EntityDeleteAction.create(persister, person, TestPerson.class).execute();
        TestPerson actual = loader.load(person.getId());

        // then
        assertThat(actual).isNull();
    }
}
