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
import persistence.core.EntityMetadataProvider;
import persistence.core.EntityScanner;
import persistence.entity.loader.EntityLoaders;
import persistence.entity.persister.EntityPersisters;

import java.sql.Types;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class EventListenerRegistryTest {
    private static EntityMetadataProvider entityMetadataProvider;
    private EventListenerRegistry eventListenerRegistry;
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
        eventListenerRegistry = new EventListenerRegistry(entityPersisters, entityLoaders);
    }


    @Test
    @DisplayName("eventListenerRegistry 의 Persist 로 entity 를 저장할 수 있다.")
    void eventListenerRegistryPersistTest() {
        final EventListenerGroup<PersistEventListener> listener = eventListenerRegistry.getListener(EventType.PERSIST);
        final Person entity = new Person("종민", 30, "jongmin4943@gmail.com");

        listener.fireEvent(new PersistEvent(entity), PersistEventListener::onPersist);

        assertThat(entity.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("eventListenerRegistry 의 MERGE 로 entity 를 변경할 수 있다.")
    void eventListenerRegistryMergeTest() {
        final EventListenerGroup<MergeEventListener> listener = eventListenerRegistry.getListener(EventType.MERGE);
        final Person entity = new Person("종민", 30, "jongmin4943@gmail.com");

        assertDoesNotThrow(() -> {
            listener.fireEvent(new MergeEvent(entity), MergeEventListener::onMerge);
        });
    }

    @Test
    @DisplayName("eventListenerRegistry 의 DELETE 로 entity 를 삭제할 수 있다.")
    void eventListenerRegistryDeleteTest() {
        final EventListenerGroup<DeleteEventListener> listener = eventListenerRegistry.getListener(EventType.DELETE);
        final Person entity = new Person("종민", 30, "jongmin4943@gmail.com");

        assertDoesNotThrow(() -> {
            listener.fireEvent(new DeleteEvent(entity), DeleteEventListener::onDelete);
        });
    }

    @Test
    @DisplayName("eventListenerRegistry 의 LOAD 로 entity 를 조회할 수 있다.")
    void eventListenerRegistryLoadTest() {
        final EventListenerGroup<LoadEventListener> listener = eventListenerRegistry.getListener(EventType.LOAD);

        final Person entity = listener.fireEventReturn(new LoadEvent<>(1L, Person.class), LoadEventListener::onLoad);

        assertSoftly(softly->{
            softly.assertThat(entity.getId()).isEqualTo(1L);
            softly.assertThat(entity.getName()).isEqualTo("min");
            softly.assertThat(entity.getAge()).isEqualTo(30);
            softly.assertThat(entity.getEmail()).isEqualTo("jongmin4943@gmail.com");
        });
    }
}
