package persistence.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.binder.AnnotationBinder;
import persistence.fake.FakeDialect;
import persistence.fake.MockConnection;
import persistence.meta.MetaModel;
import persistence.sql.QueryGenerator;

class EntityManagerFactoryTest {

    @Test
    @DisplayName("엔티티 메니저를 생성한다.")
    void createEntityManager() throws Exception {
        //given
        MetaModel metaModel = AnnotationBinder.bindMetaModel("persistence.testFixtures");
        QueryGenerator queryGenerator = QueryGenerator.of(new FakeDialect());
        EntityManagerFactory entityManagerFactory = new EntityManagerFactory(metaModel, queryGenerator, new ThreadLocalSessionContext());

        //when
        EntityManager entityManager = entityManagerFactory.openSession(new MockConnection());

        //then
        assertNotNull(entityManager);
    }

    @Test
    @DisplayName("외부 쓰레드에서 생성된 엔터티 매니저는 다른 엔터티 매니저이다.")
    void externalEntityManager() throws Exception {
        //given
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        MetaModel metaModel = AnnotationBinder.bindMetaModel("persistence.testFixtures");
        QueryGenerator queryGenerator = QueryGenerator.of(new FakeDialect());
        EntityManagerFactory entityManagerFactory = new EntityManagerFactory(metaModel, queryGenerator, new ThreadLocalSessionContext());

        //when
        EntityManager entityManager = entityManagerFactory.openSession(new MockConnection());
        EntityManager equalThreadEntityManager = entityManagerFactory.openSession(new MockConnection());
        EntityManager externalEntityManager = executorService.submit(() -> entityManagerFactory.openSession(new MockConnection())).get();


        //then
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(entityManager).isEqualTo(equalThreadEntityManager);
            softAssertions.assertThat(equalThreadEntityManager).isNotEqualTo(externalEntityManager);
        });
    }

    @Test
    @DisplayName("세션을 종료하고 다시 열면 다른 엔터티 매니저이다.")
    void reopenSession() throws Exception {
        //given
        MetaModel metaModel = AnnotationBinder.bindMetaModel("persistence.testFixtures");
        QueryGenerator queryGenerator = QueryGenerator.of(new FakeDialect());
        EntityManagerFactory entityManagerFactory = new EntityManagerFactory(metaModel, queryGenerator, new ThreadLocalSessionContext());

        //when
        EntityManager entityManager = entityManagerFactory.openSession(new MockConnection());
        entityManagerFactory.closeSession();
        EntityManager reopenSession = entityManagerFactory.openSession(new MockConnection());

        //then
        assertThat(entityManager).isNotEqualTo(reopenSession);
    }

}
