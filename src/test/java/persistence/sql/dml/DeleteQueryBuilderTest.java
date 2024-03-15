package persistence.sql.dml;

import domain.Person;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DeleteQueryBuilderTest {

    @Test
    @DisplayName("Person 객체를 delete 쿼리로 변환한다.")
    void testDeleteDml() {
        //given
        DeleteQueryBuilder deleteQueryBuilder = new DeleteQueryBuilder();
        Person person = new Person("username", 50, "test@test.com", 1);

        //when
        String query = deleteQueryBuilder.build(person).toStatementWithId(1L);

        //then
        assertThat(query).isEqualTo("delete from users where id = 1");
    }

}
