package bootstrap;

import domain.Order;
import domain.OrderItem;
import domain.Person;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class EntityBinderTest {

    @DisplayName("@Entity가 있는 클래스를 찾아서 반환한다. ")
    @Test
    void bind(){
        Binder binder = new EntityBinder();
        List<Class<?>> classes = binder.bind("domain");

        assertThat(classes).contains(Order.class, OrderItem.class, Person.class);
    }

    @DisplayName("@Entity가 없으면 빈 리스트를 반환한다. ")
    @ParameterizedTest
    @MethodSource
    void bindWhenNotEntity(String basePackage){
        Binder binder = new EntityBinder();
        List<Class<?>> classes = binder.bind(basePackage);

        assertThat(classes).isEmpty();
    }

    public static Stream<String> bindWhenNotEntity() {
        return Stream.of("database", "jdbc");
    }

}
