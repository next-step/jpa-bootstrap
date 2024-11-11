package persistence.sql.ddl.query;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.fixtures.TestLazyOrder;
import persistence.meta.Metamodel;

import static org.assertj.core.api.Assertions.assertThat;

class DropQueryBuilderTest {

    @Test
    @DisplayName("should create a DROP TABLE query")
    void build() {
        DropQueryBuilder dropQueryBuilder = new DropQueryBuilder("lazy_orders");
        String query = dropQueryBuilder.build();

        assertThat(query).isEqualTo("DROP TABLE lazy_orders if exists;");
    }
}
