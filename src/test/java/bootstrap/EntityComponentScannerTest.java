package bootstrap;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class EntityComponentScannerTest {

    @Test
    void testScanEntities() throws Exception {
        EntityComponentScanner scanner = new EntityComponentScanner();
        List<Class<?>> classes = scanner.scan("bootstrap.scantest.testpackage");

        assertAll(
                () -> assertThat(classes).hasSize(1),
                () -> assertThat(classes.get(0).getSimpleName()).isEqualTo("SimpleTestEntity")
        );
    }

    @Test
    void testInvalidPackage() throws Exception {
        EntityComponentScanner scanner = new EntityComponentScanner();
        List<Class<?>> classes = scanner.scan("invalid.package");

        assertThat(classes).isEmpty();
    }

    @Test
    void testEmptyPackage() throws Exception {
        EntityComponentScanner scanner = new EntityComponentScanner();
        List<Class<?>> classes = scanner.scan("bootstrap.scantest.testpackage.empty");

        assertThat(classes).isEmpty();
    }

    @Test
    void testScanEntitiesWithSubpackage() throws Exception {
        EntityComponentScanner scanner = new EntityComponentScanner();
        List<Class<?>> classes = scanner.scan("bootstrap.scantest.testpackage.subpackage");

        assertAll(
                () -> assertThat(classes).hasSize(1),
                () -> assertThat(classes.get(0).getSimpleName()).isEqualTo("SimpleTestEntity")
        );
    }
}
