package persistence.sql;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import persistence.sql.ComponentScanner;

@DisplayName("ComponentScanner class 의")
class ComponentScannerTest {

    @DisplayName("scan 메서드는")
    @Nested
    class Scan {
        @DisplayName("domain 패키지에 속한 클래스들을 반환한다.")
        @Test
        void scan() {


            // Given & When
            List<Class<?>> entityList = ComponentScanner.getClasses("domain");

            // Then
            assertEquals(5, entityList.size());
        }
    }
}
