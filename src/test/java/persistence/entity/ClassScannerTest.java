package persistence.entity;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("클래스 스캐너 테스트")
class ClassScannerTest {

    @Test
    @DisplayName("존재하지 않는 패키지이면 예외가 발생한다.")
    void noPackage() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> ClassScanner.scan("noPackage"));

    }

    @Test
    @DisplayName("클래스들을 스캔 한다.")
    void entity() {
        //when
        final Set<Class<?>> entityClasses = ClassScanner.scan("persistence.testFixtures");

        //then
        assertThat(entityClasses).hasSize(9);
    }

}
