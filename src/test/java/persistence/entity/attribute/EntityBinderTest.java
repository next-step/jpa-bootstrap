package persistence.entity.attribute;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static persistence.study.TestUtils.assertDoesNotThrowException;

@Nested
@DisplayName("EntityBinder 클래스의")
public class EntityBinderTest {

    @Nested
    @DisplayName("init 메소드를 명시적으로 실행하면")
    public class init {

        @Test
        @DisplayName("클래스로더가 부팅시에 해당 클래스를 먼저 로딩한다.")
        void test() {
            assertDoesNotThrowException(EntityBinder::init);
        }
    }
}
