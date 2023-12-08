package persistence.entity;

import static org.assertj.core.api.Assertions.assertThat;

import database.H2;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.entry.EntityEntry;
import persistence.entity.entry.JdbcEntityEntry;
import persistence.entity.persistentcontext.PersistenceContext;
import persistence.meta.model.AnnotationBinder;
import persistence.meta.model.ComponentScanner;
import persistence.meta.model.MetaModel;

public class EntityManagerFactoryImplTest {

  public static JdbcTemplate jdbcTemplate;

  public static PersistenceContext persistenceContext;
  public static EntityEntry entityEntry;
  public static MetaModel metaModel;

  @BeforeAll
  static void setup() throws SQLException {
    Connection connection = new H2().getConnection();
    JdbcTemplate jdbcTemplate = new JdbcTemplate(connection);
    entityEntry = new JdbcEntityEntry();

    ComponentScanner componentScanner = new ComponentScanner();

    try {
      metaModel = new AnnotationBinder(componentScanner).buildMetaModel(jdbcTemplate, "domain");
    } catch (IOException | ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  @DisplayName("EntityManagerFactoryImpl이 EntityManager를 생성한 후 가져옵니다.")
  public void getEntityManager() {
    EntityManagerFactoryImpl factory = new EntityManagerFactoryImpl(metaModel);

    EntityManager entityManager = factory.openSession();

    assertThat(entityManager).isInstanceOf(JdbcEntityManager.class);

  }

  @Test
  @DisplayName("EntityManagerFactoryImpl이 EntityManager를 생성한 후 초기화 하면 다른 EntityManager를 생성해서 가져옵니다.")
  public void clearEntityManagerAndGetEntityManager() throws SQLException {
    EntityManagerFactoryImpl factory = new EntityManagerFactoryImpl(metaModel);

    EntityManager firstEntityManager = factory.openSession();
    factory.closeSession();
    EntityManager secondEntityManager = factory.openSession();

    assertThat(firstEntityManager == secondEntityManager).isFalse();
  }
}
