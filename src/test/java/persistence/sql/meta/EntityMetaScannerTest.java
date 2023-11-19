package persistence.sql.meta;

import database.DatabaseServer;
import database.H2;
import domain.Order;
import domain.*;
import fixture.*;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.*;
import persistence.entity.EntityLoader;
import persistence.sql.ddl.DdlQueryGenerator;
import persistence.sql.dialect.Dialect;
import persistence.sql.dialect.DialectFactory;
import persistence.sql.dml.DmlQueryGenerator;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class EntityMetaScannerTest {

    @Test
    @DisplayName("domain 패키지의 Entity 클래스 전체 스캔")
    void scan() {
        EntityMetaScanner scanner = new EntityMetaScanner(new EntityScanFilter());
        List<Class<?>> scannedList = scanner.scan().stream()
                .map(EntityMeta::getInnerClass)
                .collect(Collectors.toList());

        assertThat(scannedList.size()).isEqualTo(5);
        assertThat(scannedList).contains(Order.class, OrderItem.class, Person.class, Employee.class, Department.class);
    }

    @Nested
    class EntityMetaScannerDbConnectTest {

        private DatabaseServer server;
        private JdbcTemplate jdbcTemplate;
        private DdlQueryGenerator ddlQueryGenerator;
        private DmlQueryGenerator dmlQueryGenerator;

        @BeforeEach
        void setUp() throws Exception {
            server = new H2();
            server.start();
            jdbcTemplate = new JdbcTemplate(server.getConnection());

            DialectFactory dialectFactory = DialectFactory.getInstance();
            Dialect dialect = dialectFactory.getDialect(jdbcTemplate.getDbmsName());
            dmlQueryGenerator = DmlQueryGenerator.of(dialect);
            ddlQueryGenerator = DdlQueryGenerator.of(dialect);
        }

        @AfterEach
        void tearDown() {
            EntityMetaScanner metaScanner = new EntityMetaScanner(new EntityScanFilter());
            metaScanner.scan()
                    .forEach(entityMeta -> jdbcTemplate.execute(ddlQueryGenerator.generateDropQuery(entityMeta)));
            server.stop();
        }

        @Test
        @DisplayName("Meta 정보 scan 목록을 대상으로 테이블을 생성한 후, 데이터를 입력한다")
        void ddlByScannedInfo() {
            EntityMetaScanner metaScanner = new EntityMetaScanner(new EntityScanFilter());
            metaScanner.scan()
                    .forEach(entityMeta -> jdbcTemplate.execute(ddlQueryGenerator.generateCreateQuery(entityMeta)));

            jdbcTemplate.execute(dmlQueryGenerator.generateInsertQuery(PersonFixtureFactory.getFixture()));
            jdbcTemplate.execute(dmlQueryGenerator.generateInsertQuery(OrderFixtureFactory.getFixture()));
            OrderItemFixtureFactory.getFixtures().forEach(
                    fixture -> jdbcTemplate.execute(dmlQueryGenerator.generateInsertQuery(fixture))
            );
            jdbcTemplate.execute(dmlQueryGenerator.generateInsertQuery(DepartmentFixtureFactory.getFixture()));
            EmployeeFixtureFactory.getFixtures().forEach(
                    fixture -> jdbcTemplate.execute(dmlQueryGenerator.generateInsertQuery(fixture))
            );

            EntityLoader entityLoader = EntityLoader.of(jdbcTemplate);
            Person person = entityLoader.selectOne(Person.class, 1L);
            Order order = entityLoader.selectOne(Order.class, 1L);
            OrderItem orderItem = entityLoader.selectOne(OrderItem.class, 1L);
            Department department = entityLoader.selectOne(Department.class, 1L);
            Employee employee = entityLoader.selectOne(Employee.class, 1L);

            assertThat(person).isNotNull();
            assertThat(order).isNotNull();
            assertThat(orderItem).isNotNull();
            assertThat(department).isNotNull();
            assertThat(employee).isNotNull();
        }
    }

}