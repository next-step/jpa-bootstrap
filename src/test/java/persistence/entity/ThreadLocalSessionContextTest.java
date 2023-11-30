package persistence.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.binder.AnnotationBinder;
import persistence.fake.FakeDialect;
import persistence.fake.MockConnection;
import persistence.meta.MetaModel;
import persistence.sql.QueryGenerator;

class ThreadLocalSessionContextTest {

    @Test
    @DisplayName("현재 세션을 반환한다.")
    void currentSession() {
        //given
        MetaModel metaModel = AnnotationBinder.bindMetaModel(new ClassPackageScanner("persistence.testFixtures"));
        CurrentSessionContext currentSessionContext = new ThreadLocalSessionContext();
        EntityManager entityManager = new SimpleEntityManager(metaModel, QueryGenerator.of(new FakeDialect()),
                new MockConnection());
        currentSessionContext.bind(entityManager);

        //when
        EntityManager currentSession = currentSessionContext.currentSession();

        //then
        assertThat(entityManager).isEqualTo(currentSession);
    }

    @Test
    @DisplayName("세션을 지운다")
    void removeSession() {
        //given
        MetaModel metaModel = AnnotationBinder.bindMetaModel(new ClassPackageScanner("persistence.testFixtures"));
        CurrentSessionContext currentSessionContext = new ThreadLocalSessionContext();
        EntityManager entityManager = new SimpleEntityManager(metaModel, QueryGenerator.of(new FakeDialect()),
                new MockConnection());
        currentSessionContext.bind(entityManager);

        //when
        currentSessionContext.closeCurrentSession();

        //then
        assertThat(currentSessionContext.currentSession()).isNull();
    }
}
