package persistence.sql.dml.query;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.fixtures.TestLazyOrder;
import persistence.meta.Metamodel;
import persistence.meta.MetamodelInitializer;

import static org.assertj.core.api.Assertions.assertThat;

class SelectAllQueryBuilderTest {
    @Test
    @DisplayName("Should build select all query")
    void shouldBuildSelectAllQuery() {
        Metamodel metamodel = new MetamodelInitializer(null).getMetamodel();
        String query = new SelectAllQueryBuilder().build(TestLazyOrder.class, metamodel);

        assertThat(query).isEqualTo("SELECT * FROM lazy_orders;");
    }
}
