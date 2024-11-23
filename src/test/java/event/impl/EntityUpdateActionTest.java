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
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("EntityUpdateAction 클래스 테스트")
class EntityUpdateActionTest extends TestEntityInitialize {
    private EntityLoader<TestPerson> loader;
    private EntityPersister<TestPerson> persister;

    @BeforeEach
    void setUp() throws SQLException {
        TestPersistenceConfig config = TestPersistenceConfig.getInstance();

        EntityManager entityManager = config.entityManagerFactory().entityManager();

        persister = entityManager.getEntityPersister(TestPerson.class);
        loader = entityManager.getEntityLoader(TestPerson.class);
    }

    @DisplayName("execute 함수는 persister의 update 함수를 호출한다.")
    @Test
    void execute() {
        // given
        TestPerson oldPerson = new TestPerson(1L, "catsbi", 33, "catsbi@naver.com", 123);
        persister.insert(oldPerson);
        TestPerson newPerson = new TestPerson(1L, "hansol", 34, "hansol@naver.com", 123);

        // when
        EntityUpdateAction<TestPerson> updateAction = EntityUpdateAction.create(persister, newPerson, oldPerson, TestPerson.class);
        updateAction.execute();

        TestPerson actual = loader.load(1L);

        // then
        assertAll(
                ()-> assertThat(actual.getId()).isEqualTo(1L),
                ()-> assertThat(actual.getName()).isEqualTo("hansol"),
                ()-> assertThat(actual.getAge()).isEqualTo(34),
                ()-> assertThat(actual.getEmail()).isEqualTo("hansol@naver.com")
        );
    }
}
