package persistence.bootstrap;

import database.H2ConnectionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.dialect.Dialect;
import persistence.dialect.H2Dialect;

import java.lang.reflect.Field;
import java.sql.Connection;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class MetamodelTest {
    private Metadata metadata;

    @AfterEach
    void tearDown() {
        metadata.close();
    }

    @Test
    @DisplayName("Metamodel을 생성한다.")
    void constructor() {
        // given
        final Connection connection = H2ConnectionFactory.getConnection();
        final Dialect dialect = new H2Dialect();
        metadata = new Metadata(connection, dialect, "domain");

        // when
        final Metamodel metamodel = metadata.getMetamodel();

        // then
        assertAll(
                () -> assertThat(getField(metamodel, "entityTableBinder")).isNotNull(),
                () -> assertThat(getField(metamodel, "entityLoaderBinder")).isNotNull(),
                () -> assertThat(getField(metamodel, "entityPersisterBinder")).isNotNull(),
                () -> assertThat(getField(metamodel, "collectionPersisterBinder")).isNotNull(),
                () -> assertThat(getField(metamodel, "eventListenerRegistry")).isNotNull()
        );
    }

    private Object getField(Metamodel metamodel, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        final Field field = metamodel.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(metamodel);
    }
}
