package persistence.meta.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import database.DatabaseServer;
import database.H2;
import domain.Department;
import domain.Employee;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.persistentcontext.EntityPersister;

public class MetaModelImplTest {
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
  @DisplayName("MetaModel에서 persister를 가져올 수 있습니다.")
  public void getRegisteredPersister() throws IOException, ClassNotFoundException {
    ComponentScanner componentScanner = new ComponentScanner();

    MetaModel metaModel = new AnnotationBinder(componentScanner).buildMetaModel(jdbcTemplate, "domain");

    assertAll(
        () -> assertThat(metaModel.getPersister(Department.class)).isInstanceOf(
            EntityPersister.class),
        () -> assertThat(metaModel.getPersister(Employee.class)).isInstanceOf(EntityPersister.class)
    );
  }

  @Test
  @DisplayName("MetaModel에서 존재하지 않는 persister는 가져올 수 없습니다.")
  public void notRegisteredPersisterThrowsException() throws IOException, ClassNotFoundException {
    ComponentScanner componentScanner = new ComponentScanner();

    MetaModel metaModel = new AnnotationBinder(componentScanner).buildMetaModel(jdbcTemplate, "domain");

    assertAll(
        () -> assertThrows(IllegalStateException.class, () -> metaModel.getPersister(Number.class)),
        () -> assertThrows(IllegalStateException.class, () -> metaModel.getPersister(String.class))
    );
  }
}
