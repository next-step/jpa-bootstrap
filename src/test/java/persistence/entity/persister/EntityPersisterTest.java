package persistence.entity.persister;

import domain.FixtureEntity.Person;
import domain.FixturePerson;
import mock.MockDmlGenerator;
import mock.MockJdbcTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.core.EntityMetadata;

import static org.assertj.core.api.Assertions.assertThat;

class EntityPersisterTest {

    private EntityPersister entityPersister;
    private Person fixturePerson;

    @BeforeEach
    void setUp() {
        entityPersister = EntityPersister.of(EntityMetadata.from(Person.class), new MockDmlGenerator(), new MockJdbcTemplate());
        fixturePerson = FixturePerson.create(1L);
    }

    @Test
    @DisplayName("renderInsert 를 이용해 entity 의 insert 문을 만들 수 있다.")
    void renderInsertTest() {
        final String query = entityPersister.renderInsert(fixturePerson);
        assertThat(query).isEqualTo("insert into users (nick_name, old, email) values ('min', 30, 'jongmin4943@gmail.com')");
    }

    @Test
    @DisplayName("renderUpdate 를 이용해 entity 의 update 문을 만들 수 있다.")
    void renderUpdateTest() {
        final String query = entityPersister.renderUpdate(fixturePerson);
        assertThat(query).isEqualTo("update users set nick_name='min', old=30, email='jongmin4943@gmail.com' where id=1");
    }


    @Test
    @DisplayName("renderDelete 을 이용해 entity 의 delete 문을 만들 수 있다.")
    void renderDeleteTest() {
        final String query = entityPersister.renderDelete(fixturePerson);
        assertThat(query).isEqualTo("delete from users where id=1");
    }

}
