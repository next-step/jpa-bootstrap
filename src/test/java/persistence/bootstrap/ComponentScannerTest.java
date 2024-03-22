package persistence.bootstrap;

import entity.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ComponentScannerTest {
    ComponentScanner componentScanner = new ComponentScanner();

    @Test
    void scan() throws IOException, ClassNotFoundException {
        List<Class<?>> result = componentScanner.scan("entity");

        assertThat(result).containsAll(
                List.of(
                        OldPerson1.class,
                        OldPerson2.class,
                        OldPerson3.class,
                        Person4.class
                ));
    }

}
