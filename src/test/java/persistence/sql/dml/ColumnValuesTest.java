package persistence.sql.dml;

import domain.Person;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.mock.MockEntity;
import persistence.sql.meta.EntityMeta;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ColumnValuesTest {

    @Test
    @DisplayName("컬럼목록 전체 출력")
    void columns() {
        Person 홍길동 = new Person("홍길동", 20, "aaa@bbb.com", 1);
        ColumnValues columnValues = ColumnValues.of(홍길동);
        List<String> columns = columnValues.columns();
        assertThat(columns).isEqualTo(Arrays.asList("id", "nick_name", "old", "email"));
    }

    @Test
    @DisplayName("컬럼 값 전체출력")
    void values() {
        Person 홍길동 = new Person("홍길동", 20, "aaa@bbb.com", 1);
        ColumnValues columnValues = ColumnValues.of(홍길동);
        List<String> values = columnValues.values();
        assertThat(values).isEqualTo(Arrays.asList("NULL", "'홍길동'", "20", "'aaa@bbb.com'"));
    }

    @Test
    @DisplayName("PK 조건절 조립")
    void buildPkConditions() {
        ColumnValues columnValues = ColumnValues.ofId(EntityMeta.of(Person.class), 1L);
        List<String> valueConditions = columnValues.buildValueConditions();
        assertThat(valueConditions).isEqualTo(List.of("id=1"));
    }

    @Test
    @DisplayName("AutoGen Type 제거")
    void ofFilteredAutoGenType() {
        Person 홍길동 = new Person("홍길동", 20, "aaa@bbb.com", 1);
        ColumnValues columnValues = ColumnValues.ofFilteredAutoGenType(홍길동);
        List<String> columns = columnValues.columns();
        List<String> values = columnValues.values();
        Assertions.assertAll(
                () -> assertThat(columns).isEqualTo(Arrays.asList("nick_name", "old", "email")),
                () -> assertThat(values).isEqualTo(Arrays.asList("'홍길동'", "20", "'aaa@bbb.com'"))
        );

    }

    @Test
    @DisplayName("Id Type 제거")
    void ofFilteredId() {
        Person 홍길동 = new Person("홍길동", 20, "aaa@bbb.com", 1);
        ColumnValues columnValues = ColumnValues.ofFilteredId(홍길동);
        List<String> columns = columnValues.columns();
        List<String> values = columnValues.values();
        Assertions.assertAll(
                () -> assertThat(columns).isEqualTo(Arrays.asList("nick_name", "old", "email")),
                () -> assertThat(values).isEqualTo(Arrays.asList("'홍길동'", "20", "'aaa@bbb.com'"))
        );
    }

    @Test
    @DisplayName("Id 컬럼 추출")
    void ofId() {
        ColumnValues columnValues = ColumnValues.ofId(EntityMeta.of(Person.class), 1L);
        List<String> columns = columnValues.columns();
        List<String> values = columnValues.values();
        Assertions.assertAll(
                () -> assertThat(columns).isEqualTo(List.of("id")),
                () -> assertThat(values).isEqualTo(List.of("1"))
        );
    }

    @Test
    @DisplayName("Entity Instance를 활용한 Id 컬럼 추출")
    void ofIdByEntityInstance() {
        MockEntity 테스트 = new MockEntity(2L, "테스트");
        ColumnValues columnValues = ColumnValues.ofId(테스트);
        List<String> columns = columnValues.columns();
        List<String> values = columnValues.values();
        Assertions.assertAll(
                () -> assertThat(columns).isEqualTo(List.of("id")),
                () -> assertThat(values).isEqualTo(List.of("2"))
        );
    }

}