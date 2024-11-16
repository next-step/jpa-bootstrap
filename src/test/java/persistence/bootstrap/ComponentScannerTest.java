package persistence.bootstrap;

import fixture.EntityWithId;
import fixture.EntityWithOnlyId;
import fixture.EntityWithoutDefaultConstructor;
import fixture.EntityWithoutId;
import fixture.EntityWithoutTable;
import fixture.NotEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class ComponentScannerTest {
    @Test
    @DisplayName("컴포넌트를 찾는다.")
        void scan() {
        // when
        final List<Class<?>> entityTypes = ComponentScanner.scan( "fixture");

        // then
        assertAll(
                () -> assertThat(entityTypes).hasSize(6),
                () -> assertThat(entityTypes).contains(
                        EntityWithoutId.class, EntityWithoutDefaultConstructor.class, EntityWithOnlyId.class,
                        EntityWithoutTable.class, EntityWithId.class, NotEntity.class
                )
        );
    }
}
