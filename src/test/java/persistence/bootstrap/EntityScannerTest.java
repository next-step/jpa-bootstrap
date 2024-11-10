package persistence.bootstrap;

import domain.Department;
import domain.Employee;
import domain.Order;
import domain.OrderItem;
import domain.OrderLazy;
import domain.Person;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class EntityScannerTest {
    @Test
    @DisplayName("존재하는 패키지로 엔티티 스캔을 하면 엔티티 목록을 반환한다.")
    void scan() {
        // when
        final List<Class<?>> entities = EntityScanner.scan("domain");

        // then
        assertAll(
                () -> assertThat(entities).hasSize(6),
                () -> assertThat(entities).contains(
                        Person.class, Order.class, OrderItem.class, OrderLazy.class, Department.class, Employee.class
                )
        );
    }

    @Test
    @DisplayName("존재하지 않는 패키지로 엔티티 스캔을 한면 예외를 발생한다.")
    void scan_exception() {
        // when & then
        assertThatThrownBy(() -> EntityScanner.scan("not_package"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(EntityScanner.NOT_EXISTS_PACKAGE_FAILED_MESSAGE);
    }
}
