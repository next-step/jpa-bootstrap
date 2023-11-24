package persistence.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.testFixtures.Department;
import persistence.testFixtures.NoHasEntity;

@DisplayName("EntityClassFilter 테스트")
class EntityClassFilterTest {
    @Test
    void filter() {
        //given
        Set<Class<?>> classes = new HashSet<>();
        classes.add(NoHasEntity.class);
        classes.add(Department.class);

        //when
        final Set<Class<?>> entityClass = EntityClassFilter.entityFilter(classes);

        //then
        assertThat(entityClass).hasSize(1);
    }
}
