package persistence.entity;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("클래스 스캐너 테스트")
class ClassPackageScannerTest {

    @Test
    @DisplayName("존재하지 않는 패키지이면 예외가 발생한다.")
    void noPackage() {
        ClassPackageScanner classPackageScanner = new ClassPackageScanner("noPackage");
        assertThatIllegalArgumentException()
                .isThrownBy(classPackageScanner::scan);

    }

    @Test
    @DisplayName("클래스들을 스캔 한다.")
    void entity() {
        //given
        ClassPackageScanner classPackageScanner = new ClassPackageScanner("persistence.testFixtures");

        //when
        final Set<Class<?>> entityClasses = classPackageScanner.scan();

        //then
        assertThat(entityClasses).hasSize(9);
    }

}
