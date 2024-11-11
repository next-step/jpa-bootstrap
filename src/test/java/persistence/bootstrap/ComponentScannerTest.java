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

class ComponentScannerTest {
    @Test
    @DisplayName("엔티티를 찾는다.")
    void constructor() {
        // when
        final ComponentScanner componentScanner = new ComponentScanner("domain", "fixture");

        // then
        assertAll(
                () -> assertThat(componentScanner.getEntityTypes()).hasSize(11),
                () -> assertThat(componentScanner.getEntityTypes()).contains(
                        Person.class, Order.class, OrderItem.class, OrderLazy.class, EntityWithId.class,
                        EntityWithOnlyId.class, EntityWithoutDefaultConstructor.class, EntityWithoutId.class,
                        EntityWithoutTable.class, Department.class, Employee.class
                )
        );
    }
}
