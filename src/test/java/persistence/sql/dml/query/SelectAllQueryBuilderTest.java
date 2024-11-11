package persistence.sql.dml.query;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SelectAllQueryBuilderTest {
    @Test
    @DisplayName("Should build select all query")
    void shouldBuildSelectAllQuery() {
        String query = new SelectAllQueryBuilder().build("lazy_orders");

        assertThat(query).isEqualTo("SELECT * FROM lazy_orders;");
    }
}
