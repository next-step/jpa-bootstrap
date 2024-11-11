package persistence.bootstrap;

import domain.Department;
import domain.Employee;
import domain.Order;
import domain.OrderItem;
import domain.OrderLazy;
import domain.Person;
import fixture.EntityWithId;
import fixture.EntityWithOnlyId;
import fixture.EntityWithoutDefaultConstructor;
import fixture.EntityWithoutId;
import fixture.EntityWithoutTable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class EntityHolderTest {
    @Test
    @DisplayName("엔티티를 찾아서 반환한다.")
    void constructor() {
        // when
        final EntityHolder entityHolder = new EntityHolder("domain", "fixture");

        // then
        assertAll(
                () -> assertThat(entityHolder.getEntityTypes()).hasSize(11),
                () -> assertThat(entityHolder.getEntityTypes()).contains(
                        Person.class, Order.class, OrderItem.class, OrderLazy.class, EntityWithId.class,
                        EntityWithOnlyId.class, EntityWithoutDefaultConstructor.class, EntityWithoutId.class,
                        EntityWithoutTable.class, Department.class, Employee.class
                )
        );
    }
}
