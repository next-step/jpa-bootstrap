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

import static org.junit.jupiter.api.Assertions.*;

class DefaultDeleteEventListenerTest {
    private static EntityMetadataProvider entityMetadataProvider;
    private DefaultDeleteEventListener defaultDeleteEventListener;

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
        defaultDeleteEventListener = new DefaultDeleteEventListener(new ActionQueue(), entityPersisters);
    }

    @Test
    @DisplayName("onDelete의 DeleteEvent 정보를 이용해 entity 를 변경할 수 있다.")
    void onDeleteTest() {
        final FixtureEntity.Person entity = new FixtureEntity.Person("종민", 30, "jongmin4943@gmail.com");

        assertDoesNotThrow(() ->
            defaultDeleteEventListener.onDelete(new DeleteEvent(entity))
        );
    }
}
