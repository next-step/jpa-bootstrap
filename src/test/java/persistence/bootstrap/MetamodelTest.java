package persistence.bootstrap;

import database.H2ConnectionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.dialect.Dialect;
import persistence.dialect.H2Dialect;

import java.sql.Connection;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static util.ReflectionUtils.*;

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
                () -> assertThat(getFieldValue(metamodel, "entityTableBinder")).isNotNull(),
                () -> assertThat(getFieldValue(metamodel, "entityLoaderBinder")).isNotNull(),
                () -> assertThat(getFieldValue(metamodel, "entityPersisterBinder")).isNotNull(),
                () -> assertThat(getFieldValue(metamodel, "collectionPersisterBinder")).isNotNull()
        );
    }
}
