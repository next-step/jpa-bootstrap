package database.sql.dml;

import entity.Person4;
import org.junit.jupiter.api.Test;
import persistence.entity.context.PersistentClass;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SelectByPrimaryKeyTest {
    private final SelectByPrimaryKey<Person4> selectByPrimaryKey =
            SelectByPrimaryKey.from(PersistentClass.from(Person4.class), List.of());

    @Test
    void buildSelectPrimaryKeyQuery() {
        String actual = selectByPrimaryKey.byId(1L).buildQuery();
        assertThat(actual).isEqualTo("SELECT id, nick_name, old, email FROM users WHERE id = 1");
    }
}
