package persistence.bootstrap;

import fixture.EntityWithId;
import fixture.EntityWithOnlyId;
import fixture.EntityWithoutDefaultConstructor;
import fixture.EntityWithoutId;
import fixture.EntityWithoutTable;
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
        final EntityBinder entityBinder = new EntityBinder("fixture");

        // then
        assertAll(
                () -> assertThat(entityBinder.getEntityTypes()).hasSize(5),
                () -> assertThat(entityBinder.getEntityTypes()).contains(
                        EntityWithoutId.class, EntityWithoutDefaultConstructor.class, EntityWithOnlyId.class,
                        EntityWithoutTable.class, EntityWithId.class
                )
        );
    }
}
