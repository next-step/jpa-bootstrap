package persistence.sql;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class QueryGeneratorTest {
    @Test
    @DisplayName("방언정보가 비어 있으면 예외가 발생한다.")
    void emptyEntityMeta() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> QueryGenerator.of(null));
    }

}
