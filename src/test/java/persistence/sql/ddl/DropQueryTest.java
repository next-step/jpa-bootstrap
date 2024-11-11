package persistence.sql.ddl;

import fixture.EntityWithId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.meta.EntityTable;

import static org.assertj.core.api.Assertions.*;

class DropQueryTest {
    @Test
    @DisplayName("drop 쿼리를 생성한다.")
    void drop() {
        // given
        final EntityTable entityTable = new EntityTable(EntityWithId.class);
        final DropQuery dropQuery = new DropQuery(entityTable.getTableName());

        // when
        final String sql = dropQuery.drop();

        // then
        assertThat(sql).isEqualTo("DROP TABLE IF EXISTS users");
    }
}
