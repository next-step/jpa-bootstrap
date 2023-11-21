package persistence.entity;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.binder.AnnotationBinder;
import persistence.fake.FakeDialect;
import persistence.fake.MockConnection;

class EntityManagerFactoryTest {

    @Test
    @DisplayName("엔티티 메니저를 생성한다.")
    void createEntityManager() throws Exception {
        //given
        EntityScanner scanner = new EntityScanner();
        final Set<Class<?>> scan = scanner.scan("persistence.testFixtures");
        AnnotationBinder annotationBinder = new AnnotationBinder(scan, new FakeDialect());
        EntityManagerFactory entityManagerFactory = EntityManagerFactory.create(annotationBinder.getMetaModel());

        EntityManager entityManager = entityManagerFactory.createEntityManager(new MockConnection());

        //then
        assertNotNull(entityManager);
    }

}
