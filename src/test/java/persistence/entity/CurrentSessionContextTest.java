package persistence.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.binder.AnnotationBinder;
import persistence.fake.FakeDialect;
import persistence.fake.MockConnection;
import persistence.meta.MetaModel;

class CurrentSessionContextTest {

    @Test
    @DisplayName("현재 세션을 반환한다.")
    void currentSession() {
        //given
        MetaModel metaModel = AnnotationBinder.bindMetaModel("persistence.testFixtures", new FakeDialect());
        CurrentSessionContext currentSessionContext = new CurrentSessionContext();
        EntityManager entityManager = new SimpleEntityManager(metaModel, new MockConnection());
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
        MetaModel metaModel = AnnotationBinder.bindMetaModel("persistence.testFixtures", new FakeDialect());
        CurrentSessionContext currentSessionContext = new CurrentSessionContext();
        EntityManager entityManager = new SimpleEntityManager(metaModel, new MockConnection());
        currentSessionContext.bind(entityManager);

        //when
        currentSessionContext.closeCurrentSession();

        //then
        assertThat(currentSessionContext.currentSession()).isNull();
    }
}
