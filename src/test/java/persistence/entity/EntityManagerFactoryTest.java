package persistence.entity;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.binder.AnnotationBinder;
import persistence.fake.FakeDialect;
import persistence.fake.MockConnection;
import persistence.meta.MetaModel;

class EntityManagerFactoryTest {

    @Test
    @DisplayName("엔티티 메니저를 생성한다.")
    void createEntityManager() throws Exception {
        //given
        MetaModel metaModel = AnnotationBinder.bindMetaModel("persistence.testFixtures");
        EntityManagerFactory entityManagerFactory = EntityManagerFactory.create(metaModel, new FakeDialect());

        //when
        EntityManager entityManager = entityManagerFactory.createEntityManager(new MockConnection());

        //then
        assertNotNull(entityManager);
    }

}
