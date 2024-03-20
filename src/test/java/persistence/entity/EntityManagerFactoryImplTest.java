package persistence.entity;

import database.DataSourceProperties;
import database.DatabaseServer;
import database.H2;
import database.HibernateProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.dialect.MysqlDialect;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EntityManagerFactoryImplTest {

    private DatabaseServer server;

    @BeforeEach
    void setUp() throws SQLException {
        server = new H2();
        server.start();
    }

    @Test
    @DisplayName("이미 열린 세션을 다시 열려고 하면 IllegalStateException이 발생한다.")
    void openAlreadySession() throws SQLException {
        //given
        HibernateProperties hibernateProperties = new HibernateProperties(new MysqlDialect(), server.getDataSourceProperties(), server.getConnection());
        EntityManagerFactory entityManagerFactory = new EntityManagerFactoryImpl(hibernateProperties);
        entityManagerFactory.openSession();

        //when & then
        assertThatThrownBy(entityManagerFactory::openSession)
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("연결된 세션이 없으면 세션을 생성한다.")
    void openSession() throws SQLException {
        //given
        HibernateProperties hibernateProperties = new HibernateProperties(new MysqlDialect(), server.getDataSourceProperties(), server.getConnection());
        EntityManagerFactory entityManagerFactory = new EntityManagerFactoryImpl(hibernateProperties);

        //when
        EntityManager entityManager = entityManagerFactory.openSession();

        //then
        assertThat(entityManager).isNotNull();
    }
}
