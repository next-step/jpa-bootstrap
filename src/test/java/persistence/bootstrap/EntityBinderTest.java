package persistence.bootstrap;

import fixture.EntityWithId;
import fixture.EntityWithOnlyId;
import fixture.NotEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.bootstrap.binder.EntityBinder;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class EntityBinderTest {
    @Test
    @DisplayName("엔티티 목록을 반환한다.")
    void findEntity() {
        // given
        final List<Class<?>> classes = List.of(EntityWithId.class, EntityWithOnlyId.class, NotEntity.class);

        // when
        final EntityBinder entityBinder = new EntityBinder(classes);

        // then
        assertAll(
                () -> assertThat(entityBinder.getEntityTypes()).hasSize(2),
                () -> assertThat(entityBinder.getEntityTypes()).contains(EntityWithId.class, EntityWithOnlyId.class)
        );
    }
}
