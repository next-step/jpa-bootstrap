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
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class EntityActionSetTest {
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
    @DisplayName("EntityActionSet 을 통해 EntityAction 을 관리 및 실행 할 수 있다.")
    void entityActionTest() {
        final EntityActionSet<EntityInsertAction> entityActionSet = new EntityActionSet<>();
        final Person entity = new Person("종민", 30, "jongmin4943@gmail.com");
        entityActionSet.add(new EntityInsertAction(entityPersister, entity));

        entityActionSet.executeAll();

        assertThat(entity.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("EntityActionSet 은 중복된 EntityAction 이 들어오면 기존 EntityAction 을 덮어씌운다.")
    void entityActionDuplicateTest() {
        final EntityActionSet<EntityInsertAction> entityActionSet = new EntityActionSet<>();
        final Person entity = new Person("종민", 30, "jongmin4943@gmail.com");
        entityActionSet.add(new EntityInsertAction(entityPersister, entity));

        entity.changeEmail("modify@modify.com");
        entityActionSet.add(new EntityInsertAction(entityPersister, entity));

        entityActionSet.executeAll();

        assertSoftly(softly -> {
            softly.assertThat(entity.getId()).isEqualTo(1L);
            softly.assertThat(entity.getEmail()).isEqualTo("modify@modify.com");
        });
    }
}
