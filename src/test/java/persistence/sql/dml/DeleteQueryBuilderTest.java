package persistence.sql.dml;

import domain.Person;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.meta.Table;

import static org.assertj.core.api.Assertions.assertThat;

class DeleteQueryBuilderTest {

    @Test
    @DisplayName("DeleteQuery를 만들 수 있다.")
    void buildDeleteQuery() {
        //given
        DeleteQueryBuilder deleteQueryBuilder = DeleteQueryBuilder.getInstance();

        //when
        Table table = Table.from(Person.class);
        String query = deleteQueryBuilder.build(table, table.getIdColumn(), 1L);

        //then
        assertThat(query).isEqualTo("DELETE FROM users WHERE id = 1");
    }
}
