package boot;

import domain.Department;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.support.DatabaseSetup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DatabaseSetup
class MyMetaModelTest {
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Test
    void construct() {
        MyMetaModel metaModel = new MyMetaModel(jdbcTemplate);

        assertAll(
                () -> assertThat(metaModel.getEntityMetaFrom(new Department())).isNotNull(),
                () -> assertThat(metaModel.getEntityLoader(Department.class)).isNotNull(),
                () -> assertThat(metaModel.getEntityPersister(Department.class)).isNotNull()
        );
    }
}
