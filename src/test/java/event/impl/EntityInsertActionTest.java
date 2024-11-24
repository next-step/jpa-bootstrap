package event.impl;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.common.util.CamelToSnakeConverter;
import persistence.sql.context.impl.DefaultEntityPersister;
import persistence.sql.dml.impl.SimpleMetadataLoader;
import persistence.sql.fixture.TestPerson;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("EntityInsertAction 클래스 테스트")
class EntityInsertActionTest {

    @DisplayName("동일한 엔티티로 만든 EntityInsertAction 인스턴스는 동등하다")
    @Test
    void equals() {
        // given
        EntityInsertAction<String> action1 = new EntityInsertAction<>("test", null);
        EntityInsertAction<String> action2 = new EntityInsertAction<>("test", null);

        // when
        boolean actual = action1.equals(action2);

        // then
        assertThat(actual).isTrue();
    }

    @DisplayName("다른 엔티티로 만든 EntityInsertAction 인스턴스는 동등하지 않다")
    @Test
    void notEquals() {
        // given
        EntityInsertAction<String> action1 = new EntityInsertAction<>("test1", null);
        EntityInsertAction<String> action2 = new EntityInsertAction<>("test2", null);

        // when
        boolean actual = action1.equals(action2);

        // then
        assertThat(actual).isFalse();
    }

    @DisplayName("isDelayed 함수는 엔티티저장 행위가 지연되면 안되는 전략일 경우 false를 반환한다.")
    @Test
    void isDelayed() {
        // given
        EntityInsertAction<TestPerson> action = EntityInsertAction.create(
                new DefaultEntityPersister<>(null, CamelToSnakeConverter.getInstance(), new SimpleMetadataLoader<>(TestPerson.class)),
                new TestPerson("catsbi", 33, "catsbi@naver.com", 13),
                TestPerson.class);

        // when
        boolean actual = action.isDelayed();

        // then
        assertThat(actual).isFalse();
    }

    @DisplayName("isDelayed 함수는 엔티티저장 행위가 지연되어야 하는 전략일 경우 true를 반환한다.")
    @Test
    void isDelayedWhenEntityIsNotIdentity() {
        // given
        EntityInsertAction<IdGenerateTypeIsNotIdentityEntity> action = EntityInsertAction.create(
                new DefaultEntityPersister<>(null, CamelToSnakeConverter.getInstance(), new SimpleMetadataLoader<>(IdGenerateTypeIsNotIdentityEntity.class)),
                new IdGenerateTypeIsNotIdentityEntity("catsbi"),
                IdGenerateTypeIsNotIdentityEntity.class);

        // when
        boolean actual = action.isDelayed();

        // then
        assertThat(actual).isTrue();
    }

    private static class IdGenerateTypeIsNotIdentityEntity {
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private Long id;

        private String name;

        public IdGenerateTypeIsNotIdentityEntity(String name) {
            this.name = name;
        }
    }
}
