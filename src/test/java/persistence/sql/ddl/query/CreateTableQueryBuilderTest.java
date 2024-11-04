package persistence.sql.ddl.query;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.meta.Metamodel;
import persistence.meta.MetamodelCollector;
import persistence.sql.Dialect;
import persistence.sql.H2Dialect;
import persistence.sql.ddl.fixtures.TestEntityWithAutoIdStrategy;
import persistence.sql.ddl.fixtures.TestEntityWithIdentityIdStrategy;
import persistence.sql.ddl.fixtures.TestEntityWithNullableColumns;
import persistence.sql.ddl.fixtures.TestEntityWithTransientColumn;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CreateTableQueryBuilderTest {
    private final Dialect dialect = new H2Dialect();
    private final Metamodel metamodel = new MetamodelCollector(null).getMetamodel();

    @Test
    @DisplayName("Should create create table query for TestEntityWithAutoIdStrategy")
    void createTable_TestEntityWithAutoIdStrategy() {
        String query = new CreateTableQueryBuilder(
                dialect, TestEntityWithAutoIdStrategy.class,
                metamodel, List.of()
        ).build();

        assertThat(query).isEqualTo(
                "CREATE TABLE TestEntityWithAutoIdStrategy " +
                        "(id BIGINT, PRIMARY KEY (id));");
    }

    @Test
    @DisplayName("Should create a CREATE TABLE query for TestEntityWithIdentityIdStrategy")
    void createTable_TestEntityWithIdentityIdStrategy() {
        String query = new CreateTableQueryBuilder(
                dialect, TestEntityWithIdentityIdStrategy.class,
                metamodel, List.of()
        ).build();

        assertThat(query).isEqualTo(
                "CREATE TABLE TestEntityWithIdentityIdStrategy " +
                        "(id BIGINT GENERATED BY DEFAULT AS IDENTITY, PRIMARY KEY (id));"
        );
    }

    @Test
    @DisplayName("Should create a CREATE TABLE query for TestEntityWithNullableColumns")
    void createTable_TestEntityWithNullableColumns() {
        String query = new CreateTableQueryBuilder(
                dialect, TestEntityWithNullableColumns.class,
                metamodel, List.of()
        ).build();

        assertThat(query).isEqualTo(
                "CREATE TABLE TestEntityWithNullableColumns " +
                        "(id BIGINT GENERATED BY DEFAULT AS IDENTITY, nullableColumn1 VARCHAR(255), " +
                        "nullableColumn2 VARCHAR(255), " +
                        "nonNullableColumn VARCHAR(255) NOT NULL, " +
                        "PRIMARY KEY (id));"
        );
    }

    @Test
    @DisplayName("Should create a CREATE TABLE query for TestEntityWithTransientColumn")
    void createTable_TestEntityWithTransientColumn() {
        String query = new CreateTableQueryBuilder(
                dialect, TestEntityWithTransientColumn.class,
                metamodel, List.of()
        ).build();

        assertThat(query).isEqualTo(
                "CREATE TABLE TestEntityWithTransientColumn " +
                        "(id BIGINT GENERATED BY DEFAULT AS IDENTITY, normalColumn VARCHAR(255), " +
                        "PRIMARY KEY (id));"
        );
    }
}
