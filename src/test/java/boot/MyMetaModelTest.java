package boot;

import boot.metamodel.MyMetaModel;
import domain.Department;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.support.FakeDatabaseServer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;


class MyMetaModelTest {
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        this.jdbcTemplate = new JdbcTemplate(new FakeDatabaseServer().getConnection());
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
