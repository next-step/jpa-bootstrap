package persistence.sql.dml.statement;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.dialect.H2Dialect;
import registry.EntityMetaRegistry;

@DisplayName("UPDATE 쿼리 생성 테스트")
class UpdateStatementBuilderTest {

    @Test
    @DisplayName("엔티티 객체의 업데이트 쿼리를 생성할 수 있다.")
    void canCreateUpdateQuery() {
        final EntityMetaRegistry entityMetaRegistry = EntityMetaRegistry.of(new H2Dialect());
        entityMetaRegistry.addEntityMeta(UpdateStateBuilderFixture.class);

        final UpdateStateBuilderFixture fixture = new UpdateStateBuilderFixture(1L, "updatedJames", "updated@gamil.com");

        final String updateSql = UpdateStatementBuilder.builder()
            .update(fixture, entityMetaRegistry.getEntityMeta(UpdateStateBuilderFixture.class))
            .equalById()
            .build();

        assertThat(updateSql)
            .isEqualTo("UPDATE UPDATESTATEBUILDERFIXTURE SET name = 'updatedJames', email = 'updated@gamil.com' WHERE id = 1;");
    }

    @Entity
    static class UpdateStateBuilderFixture {

        @Id
        private Long id;

        private String name;

        private String email;

        public UpdateStateBuilderFixture(Long id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }

        protected UpdateStateBuilderFixture() {

        }
    }

}
