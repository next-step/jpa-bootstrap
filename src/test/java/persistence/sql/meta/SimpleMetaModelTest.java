package persistence.sql.meta;

import domain.Order;
import domain.OrderItem;
import domain.Person;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import persistence.entity.loader.EntityLoader;
import persistence.entity.persister.EntityPersister;

@DisplayName("SimpleMetaModel class 의")
class SimpleMetaModelTest {

    @DisplayName("of 메서드는")
    @Nested
    class Of {
        @DisplayName("SimpleMetaModel 인스턴스를 생성한다.")
        @Test
        void of() {
            // Given
            String basePackage = "domain";

            // When
            SimpleMetaModel metaModel = SimpleMetaModel.of(null, List.of(Order.class, OrderItem.class, Person.class));

            // Then
            assertNotNull(metaModel);
        }

        @DisplayName("basePackage에 entity가 없으면 예외를 던진다.")
        @Test
        void of_whenEntityNotExist_thenThrowException() {
            // Given
            String basePackage = "notExist";

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> SimpleMetaModel.of(null, List.of()));
        }
    }

    @DisplayName("getEntityPersister 메서드는")
    @Nested
    class GetEntityPersister {
        @DisplayName("EntityPersister 인스턴스를 반환한다.")
        @Test
        void getEntityPersister() {
            // Given
            SimpleMetaModel metaModel = SimpleMetaModel.of(null, List.of(Order.class, OrderItem.class, Person.class));

            // When
            EntityPersister<Person> entityPersister1 = metaModel.getEntityPersister(Person.class);
            EntityPersister<Order> entityPersister2 = metaModel.getEntityPersister(Order.class);
            EntityPersister<OrderItem> entityPersister3 = metaModel.getEntityPersister(OrderItem.class);

            // Then
            assertAll(
                () -> assertNotNull(entityPersister1),
                () -> assertNotNull(entityPersister2),
                () -> assertNotNull(entityPersister3)
            );
        }
    }

    @DisplayName("getEntityLoader 메서드는")
    @Nested
    class GetEntityLoader {
        @DisplayName("EntityLoader 인스턴스를 반환한다.")
        @Test
        void getEntityLoader() {
            // Given
            SimpleMetaModel metaModel = SimpleMetaModel.of(null, List.of(Order.class, OrderItem.class, Person.class));

            // When
            EntityLoader<Person> entityLoader1 = metaModel.getEntityLoader(Person.class);
            EntityLoader<Order> entityLoader2 = metaModel.getEntityLoader(Order.class);
            EntityLoader<OrderItem> entityLoader3 = metaModel.getEntityLoader(OrderItem.class);

            // Then
            assertAll(
                () -> assertNotNull(entityLoader1),
                () -> assertNotNull(entityLoader2),
                () -> assertNotNull(entityLoader3)
            );
        }
    }
}
