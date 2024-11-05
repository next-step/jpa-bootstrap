package persistence.bootstrap;

import domain.Order;
import domain.OrderItem;
import domain.OrderLazy;
import domain.Person;
import domain.test.EntityOk;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class ComponentScannerTest {
    @Test
    @DisplayName("존재하는 패키지로 컴포넌트 스캔을 하면 엔티티 목록을 반환한다.")
    void scan() throws IOException, ClassNotFoundException {
        // given
        final ComponentScanner componentScanner = new ComponentScanner();

        // when
        final List<Class<?>> entities = componentScanner.scan("domain");

        // then
        assertAll(
                () -> assertThat(entities).hasSize(5),
                () -> assertThat(entities).contains(
                        Person.class, Order.class, OrderItem.class, OrderLazy.class, EntityOk.class)
        );
    }

    @Test
    @DisplayName("존재하지 않는 패키지로 컴포넌트 스캔을 한면 예외를 발생한다.")
    void scan_exception() throws IOException, ClassNotFoundException {
        // given
        final ComponentScanner componentScanner = new ComponentScanner();

        // when & then
        assertThatThrownBy(() -> componentScanner.scan("not_package"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ComponentScanner.NOT_EXISTS_PACKAGE_FAILED_MESSAGE);
    }
}
