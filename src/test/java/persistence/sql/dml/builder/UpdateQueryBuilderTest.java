package persistence.sql.dml.builder;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.mock.MockEntity;
import persistence.mock.PureDomain;
import persistence.sql.meta.EntityMeta;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UpdateQueryBuilderTest {

    @Test
    @DisplayName("Entity 애노테이션 미존재")
    void doNotHaveEntityAnnotation() {
        assertThrows(IllegalArgumentException.class, () -> UpdateQueryBuilder.of(EntityMeta.of(PureDomain.class)), "Update Query 빌드 대상이 아닙니다.");
    }

    @Test
    @DisplayName("기본 Update 쿼리 빌드 테스트")
    void defaultUpdate() {
        UpdateQueryBuilder updateQueryBuilder = UpdateQueryBuilder.of(EntityMeta.of(MockEntity.class));
        MockEntity 테스트 = new MockEntity(2L, "테스트");
        String updateQuery = updateQueryBuilder.buildUpdateQuery(테스트);
        assertThat(updateQuery).isEqualTo("UPDATE mockentity SET name='테스트' WHERE id=2;");
    }

}
