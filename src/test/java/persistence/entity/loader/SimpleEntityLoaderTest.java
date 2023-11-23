package persistence.entity.loader;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.meta.EntityMeta;
import persistence.sql.QueryGenerator;
import persistence.testFixtures.Person;
import util.DataBaseTestSetUp;

class SimpleEntityLoaderTest extends DataBaseTestSetUp {

    @Test
    @DisplayName("엔터티와 ResultSet을 맵핑한다.")
    void resultSetToEntity() {
        //given
        final EntityMeta entityMeta = EntityMeta.from(Person.class);
        SimpleEntityLoader loader = SimpleEntityLoader.create();

        //when
        final Person person = jdbcTemplate.queryForObject(
                QueryGenerator.of(dialect).select().findByIdQuery(entityMeta, -1L),
                (rs) -> loader.resultSetToEntity(Person.class, rs));

        //then
        assertSoftly((it) -> {
            it.assertThat(person.getId()).isEqualTo(-1L);
            it.assertThat(person.getName()).isEqualTo("user-1");
            it.assertThat(person.getAge()).isEqualTo(10);
            it.assertThat(person.getEmail()).isEqualTo("userEmail");
        });
    }

    @Test
    @DisplayName("엔터티와 load 한다.")
    void load() {
        //given
        final EntityMeta entityMeta = EntityMeta.from(Person.class);
        final SimpleEntityLoader loader = SimpleEntityLoader.create();

        //when
        final Person person = jdbcTemplate.queryForObject(
                QueryGenerator.of(dialect).select().findByIdQuery(entityMeta,-1L),
                (rs) -> loader.load(Person.class, rs));

        //then
        assertSoftly((it) -> {
            it.assertThat(person.getId()).isEqualTo(-1L);
            it.assertThat(person.getName()).isEqualTo("user-1");
            it.assertThat(person.getAge()).isEqualTo(10);
            it.assertThat(person.getEmail()).isEqualTo("userEmail");
        });
    }
}