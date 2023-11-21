package persistence.event;

import domain.FixtureEntity.Person;
import mock.MockDmlGenerator;
import mock.MockJdbcTemplate;
import org.h2.tools.SimpleResultSet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.Application;
import persistence.action.ActionQueue;
import persistence.core.EntityMetadataProvider;
import persistence.core.EntityScanner;
import persistence.entity.persister.EntityPersisters;

import java.sql.Types;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultPersisEventListenerTest {
    private static EntityMetadataProvider entityMetadataProvider;
    private DefaultPersisEventListener defaultPersisEventListener;

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
        defaultPersisEventListener = new DefaultPersisEventListener(new ActionQueue(), entityPersisters);
    }

    @Test
    @DisplayName("onPersist의 PersisEvent 정보를 이용해 entity 를 저장할 수 있다.")
    void onPersistTest() {
        final Person entity = new Person("종민", 30, "jongmin4943@gmail.com");

        defaultPersisEventListener.onPersist(new PersistEvent(entity));

        assertThat(entity.getId()).isEqualTo(1L);
    }
}
