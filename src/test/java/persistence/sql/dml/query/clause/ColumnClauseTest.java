package persistence.sql.dml.query.clause;

import domain.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.entity.EntityMappingTable;
import persistence.sql.entity.model.DomainTypes;

import static org.assertj.core.api.Assertions.assertThat;

class ColumnClauseTest {

    private DomainTypes domainTypes;

    @BeforeEach
    void setUp() {
        this.domainTypes = EntityMappingTable.from(Person.class).getDomainTypes();
    }

    @DisplayName("컬럼 쿼리를 반환한다.")
    @Test
    void columnSql() {
        ColumnClause columnClause = new ColumnClause(domainTypes.getColumnName());

        assertThat(columnClause.toSql()).isEqualTo("id,\n" +
                "nick_name,\n" +
                "old,\n" +
                "email");
    }
}