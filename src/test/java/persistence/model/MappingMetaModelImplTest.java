package persistence.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.EntityMetaDataTestSupport;
import persistence.PersonV3FixtureFactory;
import persistence.entity.persister.EntityPersister;
import persistence.sql.ddl.PersonV3;

import static org.assertj.core.api.Assertions.assertThat;

class MappingMetaModelImplTest extends EntityMetaDataTestSupport {

    @DisplayName("저장된 EntityPersistor 를 가져온다")
    @Test
    public void getEntityDescriptor() throws Exception {
        // given
        final PersonV3 person = PersonV3FixtureFactory.generatePersonV3Stub();

        // when
        final EntityPersister result = metaModel.getEntityDescriptor(person);

        // then
        assertThat(result).isNotNull();
    }

}
