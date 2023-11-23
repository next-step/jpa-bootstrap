package persistence.event;

import domain.FixtureEntity;
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

class PersistEventListenerTest {
    private static EntityMetadataProvider entityMetadataProvider;
    private PersistEventListener persistEventListener;

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
        persistEventListener = new PersistEventListener(new ActionQueue(), entityPersisters);
    }

    @Test
    @DisplayName("persistEventListener 로 PersisEvent 정보를 이용해 entity 를 저장할 수 있다.")
    void onPersistTest() {
        final FixtureEntity.Person entity = new FixtureEntity.Person("종민", 30, "jongmin4943@gmail.com");

        persistEventListener.on(new PersistEvent<>(entity));

        assertThat(entity.getId()).isEqualTo(1L);
    }
}
