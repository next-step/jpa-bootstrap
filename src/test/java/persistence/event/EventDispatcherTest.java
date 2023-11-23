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
import persistence.entity.loader.EntityLoaders;
import persistence.entity.persister.EntityPersisters;

import java.sql.Types;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class EventDispatcherTest {

    private static EntityMetadataProvider entityMetadataProvider;

    private EventDispatcher eventDispatcher;

    @BeforeAll
    static void beforeAll() {
        entityMetadataProvider = EntityMetadataProvider.from(new EntityScanner(Application.class));
    }

    @BeforeEach
    void setUp() {
        final SimpleResultSet rs = new SimpleResultSet();
        rs.addColumn("id", Types.BIGINT, 10, 0);
        rs.addColumn("nick_name", Types.VARCHAR, 255, 0);
        rs.addColumn("old", Types.INTEGER, 10, 0);
        rs.addColumn("email", Types.VARCHAR, 255, 0);
        rs.addRow(1L, "min", 30, "jongmin4943@gmail.com");
        final EntityPersisters entityPersisters = new EntityPersisters(entityMetadataProvider, new MockDmlGenerator(), new MockJdbcTemplate(rs));
        final EntityLoaders entityLoaders = new EntityLoaders(entityMetadataProvider, new MockDmlGenerator(), new MockJdbcTemplate(rs));
        eventDispatcher = new EventDispatcher(new ActionQueue(), entityPersisters, entityLoaders);
    }

    @Test
    @DisplayName("dispatch(PersisEvent) 정보를 이용해 entity 를 저장할 수 있다.")
    void dispatchPersistTest() {
        final FixtureEntity.Person entity = new FixtureEntity.Person("종민", 30, "jongmin4943@gmail.com");

        eventDispatcher.dispatch(new PersistEvent<>(entity));

        assertThat(entity.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("dispatch(MergeEvent) 정보를 이용해 entity 를 변경할 수 있다.")
    void dispatchMergeTest() {
        final FixtureEntity.Person entity = new FixtureEntity.Person("종민", 30, "jongmin4943@gmail.com");

        assertDoesNotThrow(() ->
                eventDispatcher.dispatch(new MergeEvent<>(entity, 1L))
        );
    }

    @Test
    @DisplayName("dispatch(DeleteEvent) 정보를 이용해 entity 를 변경할 수 있다.")
    void dispatchDeleteTest() {
        final FixtureEntity.Person entity = new FixtureEntity.Person("종민", 30, "jongmin4943@gmail.com");

        assertDoesNotThrow(() ->
                eventDispatcher.dispatch(new DeleteEvent<>(entity, 1L))
        );
    }

    @Test
    @DisplayName("dispatch(LoadEvent) 정보를 이용해 entity 를 조회할 수 있다.")
    void dispatchLoadTest() {
        final FixtureEntity.Person entity = eventDispatcher.dispatch(new LoadEvent<>(1L, FixtureEntity.Person.class));

        assertSoftly(softly->{
            softly.assertThat(entity.getId()).isEqualTo(1L);
            softly.assertThat(entity.getName()).isEqualTo("min");
            softly.assertThat(entity.getAge()).isEqualTo(30);
            softly.assertThat(entity.getEmail()).isEqualTo("jongmin4943@gmail.com");
        });
    }
}
