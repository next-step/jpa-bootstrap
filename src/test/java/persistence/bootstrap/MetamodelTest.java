package persistence.bootstrap;

import database.H2ConnectionFactory;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class MetamodelTest {
    @Test
    @DisplayName("Metamodel을 생성한다.")
    void constructor() {
        // given
        String basePackage = "domain";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(H2ConnectionFactory.getConnection());

        // when
        final Metamodel metamodel = new Metamodel(jdbcTemplate, basePackage);

        // then
        assertAll(
                () -> assertThat(getRegistrySize(metamodel, "entityTableRegistry")).isPositive(),
                () -> assertThat(getRegistrySize(metamodel, "entityPersisterRegistry")).isPositive(),
                () -> assertThat(getRegistrySize(metamodel, "entityLoaderRegistry")).isPositive(),
                () -> assertThat(getRegistrySize(metamodel, "collectionPersisterRegistry")).isPositive(),
                () -> assertThat(getRegistrySize(metamodel, "collectionLoaderRegistry")).isPositive()
        );
    }

    private int getRegistrySize(Metamodel metamodel, String fieldName)
            throws NoSuchFieldException, IllegalAccessException {
        final Field field = metamodel.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        final Map<String, ?> registry = (Map<String, ?>) field.get(metamodel);
        return registry.size();
    }
}
