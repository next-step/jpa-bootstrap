package persistence.entity.attribute;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Nested
@DisplayName("ComponentScanner 클래스는")
public class ComponentScannerTest {

    @Nested
    @DisplayName("scan 메소드는")
    public class scan {

        @Nested
        @DisplayName("basePackage가 주어지면")
        public class withBasePackage {
            @Test
            @DisplayName("해당 경로의 모든 클래스 정보를 반환한다")
            void returnClassMeta() {
                ComponentScanner scanner = new ComponentScanner();
                try {
                    assertThat(scanner.scan("entity")).isNotNull();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
