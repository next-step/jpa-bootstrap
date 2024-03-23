package database.sql.dml;

import app.entity.Person4;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.bootstrap.Metadata;

import static org.assertj.core.api.Assertions.assertThat;
import static testsupport.EntityTestUtils.initializer;

class SelectByPrimaryKeyTest {
    private SelectByPrimaryKey<Person4> selectByPrimaryKey;

    @BeforeEach
    void setUp() {
        Metadata metadata = initializer(null).getMetadata();
        selectByPrimaryKey = SelectByPrimaryKey.from(metadata.getPersistentClass(Person4.class), metadata);
    }

    @Test
    void buildSelectPrimaryKeyQuery() {
        String actual = selectByPrimaryKey.byId(1L).buildQuery();
        assertThat(actual).isEqualTo("SELECT id, nick_name, old, email FROM users WHERE id = 1");
    }
}
