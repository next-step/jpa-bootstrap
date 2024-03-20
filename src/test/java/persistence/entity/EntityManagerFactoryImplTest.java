package persistence.entity;

import database.HibernateProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.dialect.MysqlDialect;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EntityManagerFactoryImplTest {

    @Test
    @DisplayName("이미 열린 세션을 다시 열려고 하면 IllegalStateException이 발생한다.")
    void openAlreadySession() {
        //given
        HibernateProperties hibernateProperties = new HibernateProperties(new MysqlDialect(), null, null);
        EntityManagerFactory entityManagerFactory = new EntityManagerFactoryImpl(hibernateProperties);
        entityManagerFactory.openSession();

        //when & then
        assertThatThrownBy(entityManagerFactory::openSession)
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("세션을 생성할 때, 기존의 Thread에 의해 만들어진 세션이 없어야 만들어진다.")
    void openSession() {
        //given
        HibernateProperties hibernateProperties = new HibernateProperties(new MysqlDialect(), null, null);
        EntityManagerFactory entityManagerFactory = new EntityManagerFactoryImpl(hibernateProperties);

        //when
        EntityManager entityManager = entityManagerFactory.openSession();

        //then
        assertThat(entityManager).isNotNull();
    }

    @Test
    @DisplayName("세션을 찾을때, 연결된 세션이 없으면 IllegalStateException이 발생한다.")
    void currentSessionWhenFailure() {
        //given
        HibernateProperties hibernateProperties = new HibernateProperties(new MysqlDialect(), null, null);
        EntityManagerFactory entityManagerFactory = new EntityManagerFactoryImpl(hibernateProperties);

        //when
        assertThatThrownBy(entityManagerFactory::currentSession)
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("세션을 찾을 때, 연결된 세션이 있으면 해당 세션을 반환한다.")
    void currentSession() {
        //given
        HibernateProperties hibernateProperties = new HibernateProperties(new MysqlDialect(), null, null);
        EntityManagerFactory entityManagerFactory = new EntityManagerFactoryImpl(hibernateProperties);
        entityManagerFactory.openSession();

        //when
        EntityManager entityManager = entityManagerFactory.currentSession();

        //then
        assertThat(entityManager).isNotNull();
    }
}
