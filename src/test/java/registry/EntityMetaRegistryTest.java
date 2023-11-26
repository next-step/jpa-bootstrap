package registry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.dialect.H2ColumnType;
import persistence.sql.exception.EntityMappingException;
import persistence.sql.exception.impl.PreconditionRequiredException;
import persistence.sql.schema.meta.EntityClassMappingMeta;
import registry.fixture.Department;

@DisplayName("EntityMetaRegistry 테스트")
class EntityMetaRegistryTest {

    @Test
    @DisplayName("Entity에 대한 메타 데이터를 저장할 수 있다.")
    void saveEntityMeta() {
        final EntityMetaRegistry entityMetaRegistry = EntityMetaRegistry.of(new H2ColumnType());

        assertThatCode(() -> entityMetaRegistry.addEntityMeta(Department.class))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Entity에 대한 저장된 메타 데이터를 갖고 올 수 있다.")
    void getEntityMeta() {
        final EntityMetaRegistry entityMetaRegistry = EntityMetaRegistry.of(new H2ColumnType());

        entityMetaRegistry.addEntityMeta(Department.class);

        final EntityClassMappingMeta entityClassMappingMeta = entityMetaRegistry.getEntityMeta(Department.class);
        assertThat(entityClassMappingMeta).isNotNull();
    }

    @Test
    @DisplayName("존재하지 않는 Entity 클래스를 갖고 올 수 없다.")
    void cannotGetEntityMetaDoesNotExist() {
        final EntityMetaRegistry entityMetaRegistry = EntityMetaRegistry.of(new H2ColumnType());

        assertThatThrownBy(() -> entityMetaRegistry.getEntityMeta(Department.class))
            .isInstanceOf(PreconditionRequiredException.class)
            .hasMessage(EntityMappingException.preconditionRequired("Entity 등록").getMessage());
    }
}
