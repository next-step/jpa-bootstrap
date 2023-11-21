package persistence.meta.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import database.DatabaseServer;
import database.H2;
import domain.Department;
import domain.Employee;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.persistentcontext.EntityPersister;

public class AnnotationBinderTest {

  public static DatabaseServer server;
  public static Connection connection;

  @BeforeAll
  static void setup() throws SQLException {

    server = new H2();
    server.start();
    connection = server.getConnection();

  }

  @Test
  @DisplayName("MetaModel을 만들 수 있다.")
  public void createWithoutColumnAnnotation() throws IOException, ClassNotFoundException {
    ComponentScanner componentScanner = new ComponentScanner();

    MetaModel metaModel = new AnnotationBinder(componentScanner)
        .buildMetaModel(connection, "domain");

    assertAll(
        () -> assertThat(metaModel).isInstanceOf(MetaModelImpl.class)
    );
  }
}
