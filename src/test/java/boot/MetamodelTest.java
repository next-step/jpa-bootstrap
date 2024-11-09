package boot;

import database.H2DBConnection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

class MetamodelTest {

    @DisplayName("Metamodel를 저장하고 저장된 Map을 검증한다.")
    @Test
    void initTest() throws SQLException {
        Metamodel metamodel = new MetamodelImpl(new H2DBConnection().start());
        metamodel.init();

        assertThat(metamodel.getEntityClasses())
                .contains("Order", "OrderItem", "OrderLazy", "Order.OrderItem", "OrderLazy.OrderItem", "Person", "Department", "Employee", "Department.Employee");
    }

}
