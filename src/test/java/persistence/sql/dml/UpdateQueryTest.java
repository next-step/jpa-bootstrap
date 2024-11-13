package persistence.sql.dml;

import fixture.EntityWithId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.meta.EntityTable;

import static org.assertj.core.api.Assertions.*;

class UpdateQueryTest {
    @Test
    @DisplayName("update 쿼리를 생성한다.")
    void update() {
        // given
        final UpdateQuery updateQuery = UpdateQuery.getInstance();
        final EntityWithId entity = new EntityWithId(1L, "Jackson", 20, "test@email.com");
        final EntityTable entityTable = new EntityTable(entity.getClass());

        // when
        final String sql = updateQuery.update(entityTable, entityTable.getEntityColumns(), entity);

        // then
        assertThat(sql).isEqualTo("UPDATE entity SET id = 1, nick_name = 'Jackson', old = 20, email = 'test@email.com' WHERE id = 1");
    }
}
