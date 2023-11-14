package persistence.sql.meta;

import domain.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EntityMetaScannerTest {

    @Test
    @DisplayName("domain 패키지의 Entity 클래스 전체 스캔")
    void scan() throws Exception {
        EntityMetaScanner scanner = new EntityMetaScanner();
        List<Class<?>> scannedList = scanner.scan(EntityMeta.BASE_PACKAGE);

        assertThat(scannedList.size()).isEqualTo(5);
        assertThat(scannedList).contains(Order.class, OrderItem.class, Person.class, Employee.class, Department.class);
    }

}
