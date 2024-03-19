package boot;

import jdbc.JdbcTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.EntityManager;
import persistence.support.DatabaseSetup;
import persistence.support.FakeDatabaseServer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DatabaseSetup
class MyEntityManagerFactoryTest {
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        this.jdbcTemplate = new JdbcTemplate(new FakeDatabaseServer().getConnection());
    }

    @Test
    @DisplayName("쓰레드에 할당된 session이 존재하는 경우 예외를 던진다.")
    void openSession() {
        //given
        MyEntityManagerFactory entityManagerFactory = new MyEntityManagerFactory(jdbcTemplate);
        entityManagerFactory.openEntityManager();

        //when & then
        assertThatThrownBy(entityManagerFactory::openEntityManager)
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("쓰레드에 할당된 session이 존재하는 경우 동일한 Session을 반환한다.")
    void getCurrentSession() {
        //given
        MyEntityManagerFactory entityManagerFactory = new MyEntityManagerFactory(jdbcTemplate);
        EntityManager expected = entityManagerFactory.openEntityManager();

        //when
        EntityManager actual = entityManagerFactory.getCurrentEntityManager();

        //then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("쓰레드에 할당된 session이 존재하지 않을 경우 예외를 던진다.")
    void getCurrentSession_noSession() {
        //given
        MyEntityManagerFactory entityManagerFactory = new MyEntityManagerFactory(jdbcTemplate);

        //when & then
        assertThatThrownBy(entityManagerFactory::getCurrentEntityManager)
                .isInstanceOf(IllegalStateException.class);
    }
}
