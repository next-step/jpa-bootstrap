package persistence.testFixtures;

import persistence.entity.EntityManagerFactory;
import persistence.entity.binder.AnnotationBinder;
import persistence.fake.FakeDialect;
import persistence.meta.MetaModel;

public class EntityManagerFactoryFixture {

    public static EntityManagerFactory getEntityManagerFactory() {
        MetaModel metaModel = AnnotationBinder.bindMetaModel("persistence.testFixtures");
        return EntityManagerFactory.create(metaModel, new FakeDialect());
    }
}
