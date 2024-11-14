package util;

import database.H2ConnectionFactory;
import persistence.bootstrap.Metadata;
import persistence.dialect.Dialect;
import persistence.dialect.H2Dialect;

import java.sql.Connection;

public class TestHelper {
    private TestHelper() {
        throw new AssertionError();
    }

    public static Metadata createMetadata(String... basePackages) {
        final Connection connection = H2ConnectionFactory.getConnection();
        final Dialect dialect = new H2Dialect();
        return new Metadata(connection, dialect, basePackages);
    }
}
