package persistence.bootstrap;

import database.H2ConnectionFactory;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.dialect.Dialect;
import persistence.dialect.H2Dialect;
import persistence.entity.proxy.ProxyFactory;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class MetamodelTest {
    private Metamodel metamodel;

    @AfterEach
    void tearDown() {
        metamodel.close();
    }

    @Test
    @DisplayName("Metamodel을 생성한다.")
    void constructor() {
        // given
        final JdbcTemplate jdbcTemplate = new JdbcTemplate(H2ConnectionFactory.getConnection());
        final Dialect dialect = new H2Dialect();
        final ProxyFactory proxyFactory = ProxyFactory.getInstance();

        // when
        metamodel = new Metamodel(
                jdbcTemplate, dialect, proxyFactory, "domain", "fixture");

        // then
        assertAll(
                () -> assertThat(getBinder(metamodel, "entityTableBinder")).isNotNull(),
                () -> assertThat(getBinder(metamodel, "entityLoaderBinder")).isNotNull(),
                () -> assertThat(getBinder(metamodel, "entityPersisterBinder")).isNotNull(),
                () -> assertThat(getBinder(metamodel, "collectionPersisterBinder")).isNotNull()
        );
    }

    private Object getBinder(Metamodel metamodel, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        final Field field = metamodel.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(metamodel);
    }
}
