package persistence.sql.ddl.builder;

import fixtures.EntityFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import persistence.DatabaseTest;
import persistence.entity.attribute.EntityAttribute;
import persistence.entity.attribute.EntityAttributes;
import persistence.sql.infra.H2SqlConverter;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static persistence.sql.common.DDLType.CREATE;

@Nested
@DisplayName("CreateDDLQueryBuilder 클래스의")
public class CreateDDLQueryBuilderTest extends DatabaseTest {
    private final EntityAttributes entityAttributes = new EntityAttributes();

    @Nested
    @DisplayName("prepareStatement 메소드는")
    class prepareStatement {
        @Nested
        @DisplayName("유효한 엔티티 정보가 주어지면")
        class withValidEntity {
            @Test
            @DisplayName("CREATE DDL을 리턴한다.")
            void returnDDL() {
                //given
                EntityAttribute entityAttribute = entityAttributes.findEntityAttribute(EntityFixtures.SampleOneWithValidAnnotation.class);

                //when
                String ddl = DDLQueryBuilderFactory.createQueryBuilder(CREATE)
                        .prepareStatement(entityAttribute, new H2SqlConverter());

                //then
                assertThat(ddl).isEqualTo("CREATE TABLE entity_name ( id BIGINT GENERATED BY DEFAULT AS IDENTITY, name VARCHAR(200), old INTEGER NOT NULL );");
            }
        }
    }
}