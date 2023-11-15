package persistence.sql.meta;

import domain.Person;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.EntityKey;

import static org.assertj.core.api.Assertions.assertThat;

class EntityScanFilterTest {

    @Test
    @DisplayName("엔티티 클래스의 경우 매칭결과 참을 반환한다")
    void matchTrue() {
        EntityScanFilter entityScanFilter = new EntityScanFilter();
        assertThat(entityScanFilter.match(Person.class)).isTrue();
    }

    @Test
    @DisplayName("엔티티 클래스가 아닌 경우 매칭결과 거짓을 반환한다")
    void matchFalse() {
        EntityScanFilter entityScanFilter = new EntityScanFilter();
        assertThat(entityScanFilter.match(EntityKey.class)).isFalse();
    }

}
