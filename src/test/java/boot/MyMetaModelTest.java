package boot;

import domain.Department;
import domain.Employee;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.entity.EntityMeta;
import persistence.support.DatabaseSetup;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

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
        Map<Class<?>, EntityMeta> models = metaModel.getModels();

        assertThat(models.keySet()).contains(Department.class, Employee.class);
        assertThat(metaModel.getEntityPersister(Department.class)).isNotNull();
        assertThat(metaModel.getEntityLoader(Department.class)).isNotNull();
    }
}
