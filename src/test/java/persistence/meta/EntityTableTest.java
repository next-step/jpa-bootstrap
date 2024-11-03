package persistence.meta;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.fixture.EntityWithId;
import persistence.fixture.NotEntity;

import static org.assertj.core.api.Assertions.*;

class EntityTableTest {
    @Test
    @DisplayName("@Entity 애노테이션이 존재하는 클래스로 인스턴스를 생성한다.")
    void constructor() {
        // when
        final EntityTable entityTable = new EntityTable(EntityWithId.class);

        // then
        assertThat(entityTable).isNotNull();
    }

    @Test
    @DisplayName("@Entity 애노테이션이 존재하지 않는 클래스로 인스턴스를 생성하면 예외를 발생한다.")
    void constructor_exception() {
        // when & then
        assertThatThrownBy(() -> new EntityTable(NotEntity.class))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(EntityTable.NOT_ENTITY_FAILED_MESSAGE);
    }

    @Test
    @DisplayName("id 값을 반환한다.")
    void getIdValue() {
        // given
        final EntityWithId entityWithId = new EntityWithId(1L, "Jaden", 30, "test@email.com");
        final EntityTable entityTable = new EntityTable(entityWithId);

        // when
        final Object idValue = entityTable.getIdValueWithQuotes();

        // then
        assertThat(idValue).isEqualTo("1");
    }
}
