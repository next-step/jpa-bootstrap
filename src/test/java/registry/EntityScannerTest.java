package registry;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("EntityScanner 테스트")
class EntityScannerTest {

    @Test
    @DisplayName("특정 경로 하위의 @Entity 어노테이션이 있는 모든 클래스들을 찾을 수 있다.")
    void canFindAllEntityAnnotationClass() throws IOException, ClassNotFoundException {
        EntityScanner entityScanner = new EntityScanner();

        final List<Class<?>> scannedEntity = entityScanner.scan("registry.fixture");
        assertThat(scannedEntity.size()).isEqualTo(2);
    }
}
