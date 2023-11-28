package persistence.testFixtures;

import persistence.entity.EntityManagerFactory;
import persistence.entity.ThreadLocalSessionContext;
import persistence.entity.binder.AnnotationBinder;
import persistence.fake.FakeDialect;
import persistence.meta.MetaModel;
import persistence.sql.QueryGenerator;

public class EntityManagerFactoryFixture {

    public static EntityManagerFactory getEntityManagerFactory() {
        MetaModel metaModel = AnnotationBinder.bindMetaModel("persistence.testFixtures");
        return new EntityManagerFactory(metaModel, QueryGenerator.of(new FakeDialect()), new ThreadLocalSessionContext());
    }
}
