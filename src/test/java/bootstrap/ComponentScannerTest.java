package bootstrap;

import database.DataSourceProperties;
import database.DatabaseServer;
import database.H2;
import domain.Order;
import domain.OrderItem;
import domain.Person;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class ComponentScannerTest {

    @DisplayName("패키지안의 클래스를 찾는다.")
    @Test
    void componentScannerTest() {
        //given
        //when
        List<Class<?>> domainClazz = ComponentScanner.scan("domain");
        List<Class<?>> databaseClazz = ComponentScanner.scan("database");

        //then
        assertAll(
                () ->assertThat(domainClazz).contains(Order.class, OrderItem.class, Person.class),
                () -> assertThat(databaseClazz).contains(DatabaseServer.class, DataSourceProperties.class, H2.class)
        );
    }

}
