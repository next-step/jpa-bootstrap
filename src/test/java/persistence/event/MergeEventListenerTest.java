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

class MergeEventListenerTest {
    private static EntityMetadataProvider entityMetadataProvider;
    private MergeEventListener mergeEventListener;

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
        mergeEventListener = new MergeEventListener(new ActionQueue(), entityPersisters);
    }

    @Test
    @DisplayName("mergeEventListener 에 MergeEvent 를 전달 할 수 있다.")
    void onMergeTest() {
        final FixtureEntity.Person entity = new FixtureEntity.Person(1L,"종민", 30, "jongmin4943@gmail.com");

        assertDoesNotThrow(() ->
                mergeEventListener.on(new MergeEvent<>(entity, 1L))
        );
    }

}
