package persistence.bootstrap;

import database.H2ConnectionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import persistence.dialect.Dialect;
import persistence.dialect.H2Dialect;
import persistence.entity.manager.factory.EntityManagerFactory;

import java.sql.Connection;

import static org.assertj.core.api.Assertions.*;

class MetadataTest {
    private Metadata metadata;

    @AfterEach
    void tearDown() {
        metadata.close();
    }

    @Test
    void getEntityManagerFactory() {
        // given
        final Connection connection = H2ConnectionFactory.getConnection();
        final Dialect dialect = new H2Dialect();
        metadata = new Metadata(connection, dialect, "domain");

        // when
        final EntityManagerFactory entityManagerFactory = metadata.getEntityManagerFactory();

        // then
        assertThat(entityManagerFactory).isNotNull();
    }
}
