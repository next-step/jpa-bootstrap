package persistence.entity;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Set;
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
        final Set<Class<?>> scanClass = ClassScanner.scan("persistence.testFixtures");
        final Set<Class<?>> entityClasses = EntityClassFilter.entityFilter(scanClass);
        MetaModel metaModel = AnnotationBinder.bindMetaModel(entityClasses, new FakeDialect());
        EntityManagerFactory entityManagerFactory = EntityManagerFactory.create(metaModel);

        //when
        EntityManager entityManager = entityManagerFactory.createEntityManager(new MockConnection());

        //then
        assertNotNull(entityManager);
    }

}
