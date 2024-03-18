package bootstrap;

import domain.Order;
import domain.OrderItem;
import domain.Person;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ComponentScannerTest {

    @DisplayName("패키지안의 클래스를 찾는다.")
    @Test
    void componentScannerTest() throws IOException, ClassNotFoundException {
        //given
        ComponentScanner scanner = new ComponentScanner();
        //when
        List<Class<?>> persistence = scanner.scan("domain");

        //then
        assertThat(persistence).contains(Order.class, OrderItem.class, Person.class);
    }

}
