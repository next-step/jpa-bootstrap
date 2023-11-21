package persistence.action;


import domain.FixtureEntity.Person;
import mock.MockDmlGenerator;
import mock.MockJdbcTemplate;
import org.h2.tools.SimpleResultSet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.Application;
import persistence.core.EntityMetadataProvider;
import persistence.core.EntityScanner;
import persistence.entity.persister.EntityPersister;
import persistence.entity.persister.EntityPersisters;

import java.sql.Types;

import static org.assertj.core.api.Assertions.assertThat;

class EntityInsertActionTest {
    private static EntityMetadataProvider entityMetadataProvider;
    private EntityPersister entityPersister;

    @BeforeAll
    static void beforeAll() {
        entityMetadataProvider = EntityMetadataProvider.from(new EntityScanner(Application.class));
    }

    @BeforeEach
    void setUp() {
        final SimpleResultSet rs = new SimpleResultSet();
        rs.addColumn("id", Types.BIGINT, 10, 0);
        rs.addRow(1L);
        final EntityPersisters entityPersisters = new EntityPersisters(entityMetadataProvider, new MockDmlGenerator(), new MockJdbcTemplate(rs));
        entityPersister = entityPersisters.getEntityPersister(Person.class);
    }
    @Test
    @DisplayName("EntityInsertAction 을 통해 entity 를 저장할 수 있다.")
    void entityInsertActionTest() {
        final Person entity = new Person("종민", 30, "jongmin4943@gmail.com");
        final EntityInsertAction entityInsertAction = new EntityInsertAction(entityPersister, entity);

        entityInsertAction.execute();

        assertThat(entity.getId()).isEqualTo(1L);
    }
}