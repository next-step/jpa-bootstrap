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
import persistence.core.EntityMetadataProvider;
import persistence.core.EntityScanner;
import persistence.entity.loader.EntityLoaders;

import java.sql.Types;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

class LoadEventListenerTest {
    private static EntityMetadataProvider entityMetadataProvider;
    private LoadEventListener loadEventListener;

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
        final EntityLoaders entityLoaders = new EntityLoaders(entityMetadataProvider, new MockDmlGenerator(), new MockJdbcTemplate(rs));
        loadEventListener = new LoadEventListener(entityLoaders);
    }

    @Test
    @DisplayName("loadEventListener 로 LoadEvent 정보를 이용해 entity 를 조회할 수 있다.")
    void onLoadTest() {
        final FixtureEntity.Person entity = loadEventListener.on(new LoadEvent<>(1L, FixtureEntity.Person.class));

        assertSoftly(softly->{
            softly.assertThat(entity.getId()).isEqualTo(1L);
            softly.assertThat(entity.getName()).isEqualTo("min");
            softly.assertThat(entity.getAge()).isEqualTo(30);
            softly.assertThat(entity.getEmail()).isEqualTo("jongmin4943@gmail.com");
        });
    }
}
