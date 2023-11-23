package persistence.sql.dml;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.dialect.Dialect;
import persistence.exception.NoEntityException;
import persistence.fake.FakeDialect;
import persistence.fake.UpperStringDirect;
import persistence.sql.QueryGenerator;
import persistence.testFixtures.NoAutoIncrementPerson;
import persistence.testFixtures.Person;

class InsertQueryBuilderTest {

    private Dialect dialect;

    @BeforeEach
    void setUp() {
        dialect = new FakeDialect();
    }

    @Test
    @DisplayName("엔터티 어노테이션이 없을 경우 예외가 발생한다.")
    void noEntityAnnotation() {
        //given
        QueryGenerator query = QueryGenerator.of(dialect);

        //when
        assertThatExceptionOfType(NoEntityException.class).isThrownBy(() -> {
            query.insert().build(String.class, "test");
        });
    }

    @Test
    @DisplayName("엔터티 클래스가 없을 경우 예외가 발생한다.")
    void noEntityClass() {
        //given
        QueryGenerator query = QueryGenerator.of(dialect);

        //when
        assertThatExceptionOfType(NoEntityException.class).isThrownBy(() -> {
            query.insert().build(null, "test");
        });
    }

    @Test
    @DisplayName("insert 쿼리를 생성한다.")
    void insert() {
        //given
        QueryGenerator query = QueryGenerator.of(dialect);
        Person person = new Person("name", 3, "kbh@gm.com");

        //when
        String sql = query.insert().build(Person.class, person);

        //then
        assertThat(sql).isEqualTo("INSERT INTO users (nick_name, old, email) VALUES ('name', 3, 'kbh@gm.com')");
    }

    @Test
    @DisplayName("다른 방언으로 insert 쿼리를 생성한다.")
    void insertDirect() {
        //given
        QueryGenerator query = QueryGenerator.of(new UpperStringDirect());
        Person person = new Person("name", 3, "kbh@gm.com");

        //when
        String sql = query.insert().build(Person.class, person);

        //then
        assertThat(sql).isEqualTo("INSERT INTO USERS (nick_name, old, email) VALUES ('name', 3, 'kbh@gm.com')");
    }

    @Test
    @DisplayName("id 전략이 자동생성이 아닌 insert 쿼리를 생성한다.")
    void insertNotAutoIncrement() {
        //given
        QueryGenerator query = QueryGenerator.of(new UpperStringDirect());
        NoAutoIncrementPerson person = new NoAutoIncrementPerson(3L, "name", 3, "kbh@gm.com");

        //when
        String sql = query.insert().build(NoAutoIncrementPerson.class, person);

        //then
        assertThat(sql).isEqualTo("INSERT INTO USERS (id, nick_name, old, email) VALUES (3, 'name', 3, 'kbh@gm.com')");
    }

    @Test
    @DisplayName("id 전략이 자동생성이 아닌 경우는 id가 필수이다")
    void insertNotAutoIncrementIdNull() {
        //given
        QueryGenerator query = QueryGenerator.of(new UpperStringDirect());
        NoAutoIncrementPerson person = new NoAutoIncrementPerson(null, "name", 3, "kbh@gm.com");

        assertThatIllegalArgumentException().isThrownBy(() -> {
            query.insert().build(NoAutoIncrementPerson.class, person);
        });
    }


}
