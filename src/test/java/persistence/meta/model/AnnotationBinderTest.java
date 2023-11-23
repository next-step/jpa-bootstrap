package persistence.meta.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import database.DatabaseServer;
import database.H2;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class AnnotationBinderTest {

  public static DatabaseServer server;
  public static Connection connection;
  public static JdbcTemplate jdbcTemplate;
  @BeforeAll
  static void setup() throws SQLException {

    server = new H2();
    server.start();
    connection = server.getConnection();
    jdbcTemplate = new JdbcTemplate(connection);

  }

  @Test
  @DisplayName("MetaModel을 만들 수 있다.")
  public void createWithoutColumnAnnotation() throws IOException, ClassNotFoundException {
    ComponentScanner componentScanner = new ComponentScanner();

    MetaModel metaModel = new AnnotationBinder(componentScanner)
        .buildMetaModel(jdbcTemplate, "domain");

    assertAll(
        () -> assertThat(metaModel).isInstanceOf(MetaModelImpl.class)
    );
  }
}
