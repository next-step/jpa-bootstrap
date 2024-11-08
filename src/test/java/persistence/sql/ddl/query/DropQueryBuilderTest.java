package persistence.sql.ddl.query;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.fixtures.TestLazyOrder;
import persistence.meta.Metamodel;
import persistence.meta.MetamodelInitializer;

import static org.assertj.core.api.Assertions.assertThat;

class DropQueryBuilderTest {

    @Test
    @DisplayName("should create a DROP TABLE query")
    void build() {
        Metamodel metamodel = new MetamodelInitializer(null).getMetamodel();
        DropQueryBuilder dropQueryBuilder = new DropQueryBuilder(TestLazyOrder.class, metamodel);
        String query = dropQueryBuilder.build();

        assertThat(query).isEqualTo("DROP TABLE lazy_orders if exists;");
    }
}
