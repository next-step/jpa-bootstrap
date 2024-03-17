package persistence.sql.meta;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("ComponentScanner class 의")
class ComponentScannerTest {

    @DisplayName("scan 메서드는")
    @Nested
    class Scan {
        @DisplayName("domain 패키지에 속한 클래스들을 반환한다.")
        @Test
        void scan() throws ClassNotFoundException {


            // Given & When
            List<Class<?>> entityList = ComponentScanner.scan("domain");

            // Then
            assertEquals(3, entityList.size());
        }
    }
}
