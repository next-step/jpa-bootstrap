package domain;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import database.DatabaseServer;
import database.H2;
import domain.helper.DepartmentSqlHelper;
import java.sql.SQLException;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.EntityManager;
import persistence.entity.EntityManagerFactory;

class DepartmentTest {

    private DatabaseServer server;
    private JdbcTemplate jdbcTemplate;
    private EntityManager entityManager;

    private Long id = 1L;

    private String teamName = "팀이름";

    private Department department = new Department(id, teamName, null);

    @BeforeEach
    void init() throws SQLException {
        테이블_생성();

        Employee item1 = new Employee(11L, "김갑돌");
        Employee item2 = new Employee(12L, "김갑수");
        Employee item3 = new Employee(13L, "김갑순");

        jdbcTemplate.execute(DepartmentSqlHelper.부서_저장하는_쿼리(department));
        jdbcTemplate.execute(DepartmentSqlHelper.직원_저장하는_쿼리(item1, department));
        jdbcTemplate.execute(DepartmentSqlHelper.직원_저장하는_쿼리(item2, department));
        jdbcTemplate.execute(DepartmentSqlHelper.직원_저장하는_쿼리(item3, department));

        entityManager = EntityManagerFactory.of(server.getConnection());
    }

    @Test
    @DisplayName("부서 조회시 부서에 포함된 직원까지 조회")
    void departmentFindLazy() {
        //given
        final long id = 1L;

        //when
        Department result = entityManager.find(Department.class, id);
        Employee item1 = result.getEmployees().get(0);
        Employee item2 = result.getEmployees().get(1);
        Employee item3 = result.getEmployees().get(2);

        //then
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(result.getId()).isEqualTo(id);
            softAssertions.assertThat(result.getName()).isEqualTo(teamName);
            softAssertions.assertThat(item1.getName()).isEqualTo("김갑돌");
            softAssertions.assertThat(item2.getName()).isEqualTo("김갑수");
            softAssertions.assertThat(item3.getName()).isEqualTo("김갑순");
        });
    }

    private void 테이블_생성() throws SQLException {
        server = new H2();
        jdbcTemplate = new JdbcTemplate(server.getConnection());

        jdbcTemplate.execute(DepartmentSqlHelper.부서_테이블_생성_쿼리());
        jdbcTemplate.execute(DepartmentSqlHelper.직원_테이블_생성_쿼리());
    }
}