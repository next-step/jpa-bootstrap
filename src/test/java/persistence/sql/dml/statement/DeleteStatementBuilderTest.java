package persistence.sql.dml.statement;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.ddl.generator.fixture.PersonV3;
import persistence.sql.dialect.H2Dialect;
import persistence.sql.dml.clause.operator.EqualOperator;
import persistence.sql.dml.clause.predicate.WherePredicate;
import registry.EntityMetaRegistry;

@DisplayName("DELETE 문 생성 테스트")
class DeleteStatementBuilderTest {

    @Test
    @DisplayName("Where절을 통해 조건이 있는 DELETE 문을 생성할 수 있다.")
    void canBuildSelectStatementWhere() {
        final EntityMetaRegistry entityMetaRegistry = EntityMetaRegistry.of(new H2Dialect());
        entityMetaRegistry.addEntityMeta(PersonV3.class);

        final String selectStatement = DeleteStatementBuilder.builder()
            .delete(entityMetaRegistry.getEntityMeta(PersonV3.class))
            .where(WherePredicate.of("id", 1L, new EqualOperator()))
            .build();

        assertThat(selectStatement).isEqualTo("DELETE FROM USERS WHERE id = 1");
    }
}
