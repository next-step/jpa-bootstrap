package persistence.bootstrap;

import app.entity.OldPerson1;
import app.entity.OldPerson2;
import app.entity.OldPerson3;
import app.entity.Person4;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ComponentScannerTest {
    ComponentScanner componentScanner = new ComponentScanner();

    @Test
    void scan() {
        List<Class<?>> result = componentScanner.scan("app.entity");

        assertThat(result).containsAll(
                List.of(
                        OldPerson1.class,
                        OldPerson2.class,
                        OldPerson3.class,
                        Person4.class
                ));
    }

}
