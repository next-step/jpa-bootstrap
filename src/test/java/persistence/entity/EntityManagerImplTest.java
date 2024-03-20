package persistence.entity;

import bootstrap.MetaModelImpl;
import database.DatabaseServer;
import database.H2;
import domain.Department;
import domain.Person;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.column.Columns;
import persistence.sql.column.IdColumn;
import persistence.sql.column.TableColumn;
import persistence.sql.ddl.CreateQueryBuilder;
import persistence.sql.ddl.DropQueryBuilder;
import persistence.sql.dialect.Dialect;
import persistence.sql.dialect.MysqlDialect;
import persistence.sql.dml.InsertQueryBuilder;

import java.sql.SQLException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class EntityManagerImplTest {

	private JdbcTemplate jdbcTemplate;
	private TableColumn table;
	private Dialect dialect;
	private EntityManager entityManager;
	private PersistenceContext persistContext;
	private EntityLoader entityLoader;

	@BeforeEach
	void setUp() throws SQLException {
		DatabaseServer server = new H2();
		server.start();
		jdbcTemplate = new JdbcTemplate(server.getConnection());
		Class<Person> personEntity = Person.class;
		table = new TableColumn(personEntity);
		dialect = new MysqlDialect();
		persistContext = new HibernatePersistContext();
		entityLoader = new EntityLoaderImpl(jdbcTemplate);
		entityManager = new EntityManagerImpl(
			dialect,
			persistContext,
			new MetaModelImpl(jdbcTemplate, dialect, "domain")
		);

		createAllTable(personEntity);
	}

	private void createAllTable(Class<Person> personEntity) {
		Columns columns = new Columns(personEntity.getDeclaredFields());
		IdColumn idColumn = new IdColumn(personEntity.getDeclaredFields());

		CreateQueryBuilder createQueryBuilder = new CreateQueryBuilder(table, columns, idColumn, dialect);

		String createQuery = createQueryBuilder.build();
		jdbcTemplate.execute(createQuery);
	}

	@AfterEach
	void tearDown() {
		DropQueryBuilder dropQueryBuilder = new DropQueryBuilder(table);
		String dropQuery = dropQueryBuilder.build();
		jdbcTemplate.execute(dropQuery);
	}

	@DisplayName("find 메서드를 통해 id에 해당하는 Person 객체를 찾는다.")
	@Test
	void find() {
		// given
		Long id = 1L;
		Person person = new Person(id, "John", 25, "qwer@asdf.com", 1);

		String insertQuery = new InsertQueryBuilder(dialect).build(person);
		jdbcTemplate.execute(insertQuery);

		// when
		Person findPerson = entityManager.find(Person.class, id);

		// then
		assertAll(
			() -> assertThat(person).isNotNull(),
			() -> assertThat(findPerson.getId()).isEqualTo(person.getId()),
			() -> assertThat(findPerson.getName()).isEqualTo(person.getName()),
			() -> assertThat(findPerson.getAge()).isEqualTo(person.getAge()),
			() -> assertThat(findPerson.getEmail()).isEqualTo(person.getEmail())
		);
	}

	@DisplayName("persist 메서드를 통해 Person 객체를 저장한다. - autho_increment인 경우 id가 1씩 증가한다.")
	@Test
	void persist() {
		// given
		Person person = new Person("John", 99, "john@test.com", 1);

		// when
		entityManager.persist(person);

		// then
		Person findPerson = entityManager.find(Person.class, person.getId());
		assertAll(
			() -> assertThat(findPerson).isNotNull(),
			() -> assertThat(findPerson.getId()).isEqualTo(1L),
			() -> assertThat(findPerson.getName()).isEqualTo(person.getName()),
			() -> assertThat(findPerson.getAge()).isEqualTo(person.getAge()),
			() -> assertThat(findPerson.getEmail()).isEqualTo(person.getEmail())
		);
	}

	@DisplayName("entityManager.persist() 메서드를 통해 Person 객체를 저장한다. - persistContext에 저장된다.")
	@Test
	void persistContext() {
		// given
		Person person = new Person(1L, "John", 99, "john@test.com", 1);

		// when
		entityManager.persist(person);

		// then
		Optional<Person> findPerson = persistContext.getEntity(Person.class, person.getId());

		assertAll(
			() -> assertThat(findPerson).isPresent(),
			() -> assertThat(findPerson.get()).isEqualTo(person)
		);

	}

	@DisplayName("persist 메서드를 통해 Person 객체를 저장한다. - id가 있다면 증가하지 않는다.")
	@Test
	void persistWhenHasId() {
		// given
		Person person = new Person(1L, "John", 99, "john@test.com", 1);

		// when
		entityManager.persist(person);

		// then
		Person findPerson = entityManager.find(Person.class, person.getId());
		assertAll(
			() -> assertThat(findPerson).isNotNull(),
			() -> assertThat(findPerson.getId()).isEqualTo(person.getId()),
			() -> assertThat(findPerson.getName()).isEqualTo(person.getName()),
			() -> assertThat(findPerson.getAge()).isEqualTo(person.getAge()),
			() -> assertThat(findPerson.getEmail()).isEqualTo(person.getEmail())
		);
	}

	@DisplayName("remove 메서드를 통해 Person 객체를 삭제한다.")
	@Test
	void remove() {
		// given
		Person person = new Person(1L, "John", 99, "john@test.com", 1);
		entityManager.persist(person);

		// when
		entityManager.remove(person);
		entityManager.flush();

		// then
		assertThatThrownBy(() -> entityManager.find(Person.class, person.getId()))
			.isInstanceOf(RuntimeException.class);
	}

	@DisplayName("remove 메서드를 통해 Person 객체를 삭제한다. - persistContext에서도 삭제된다.")
	@Test
	void removeContext() {
		// given
		Person person = new Person(1L, "John", 99, "john@test.com", 1);
		entityManager.persist(person);

		// when
		entityManager.remove(person);

		// then
		Optional<Person> findPerson = persistContext.getEntity(Person.class, person.getId());
		assertThat(findPerson).isEmpty();
	}

	@DisplayName("merge 메서드를 통해 Person 객체를 수정한다.")
	@Test
	void merge() {
		// given
		Person person = new Person(1L, "John", 99, "john@test.com", 1);
		entityManager.persist(person);

		// when
		person.setName("John2");
		entityManager.merge(person);

		// then
		Person findPerson = entityManager.find(Person.class, person.getId());
		assertThat(findPerson.getName()).isEqualTo("John2");
	}

	@DisplayName("merge 메서드를 통해 Person 객체를 수정한다. - persistContext에서 id는 그대로고 엔티티만 수정된다.")
	@Test
	void updateContext() {
		// given
		Person person = new Person(1L, "John", 99, "john@test.com", 1);
		entityManager.persist(person);

		// when
		person.setName("John2");
		entityManager.merge(person);

		// then
		Optional<Person> findPerson = persistContext.getEntity(Person.class, person.getId());
		assertAll(
			() -> assertThat(findPerson).isPresent(),
			() -> assertThat((findPerson.get()).getId()).isEqualTo(1L),
			() -> assertThat((findPerson.get()).getName()).isEqualTo("John2")
		);
	}

	@DisplayName("flush 메서드를 통해 persistContext에 저장된 이벤트를 실행한다.")
	@Test
	void flush() {
		// given
		Person person = new Person("John", 99, "john@test.com", 1);
		entityManager.persist(person);
		person.setName("John2");
		entityManager.merge(person);

		Person person1 = entityLoader.find(Person.class, 1L);

		assertThat(person1.getName()).isEqualTo("John");

		// when
		entityManager.flush();
		Person findPerson = entityLoader.find(Person.class, 1L);

		// then
		assertAll(
			() -> assertThat(findPerson.getId()).isEqualTo(1L),
			() -> assertThat(findPerson.getName()).isEqualTo("John2")
		);
	}

	@DisplayName("department 조회시 employee도 같이 조회한다.")
	@Test
	void departTest() {
		//given
		createAllTable();

		jdbcTemplate.execute("insert into department (id, name) values (1, 'depart123')");
		jdbcTemplate.execute("insert into employee (id, name, department_id) values (1, 'hong', 1)");
		jdbcTemplate.execute("insert into employee (id, name, department_id) values (2, 'kim', 1)");


		// when
		entityManager.flush();
		Department department = entityManager.find(Department.class, 1L);

		// then
		assertAll(
				() -> assertThat(department.getId()).isEqualTo(1L),
				() -> assertThat(department.getName()).isEqualTo("depart123"),
				() -> assertThat(department.getEmployees()).hasSize(2),
				() -> assertThat(department.getEmployees().get(0).getId()).isEqualTo(1),
				() -> assertThat(department.getEmployees().get(0).getName()).isEqualTo("hong"),
				() -> assertThat(department.getEmployees().get(1).getId()).isEqualTo(2),
				() -> assertThat(department.getEmployees().get(1).getName()).isEqualTo("kim")
		);
	}

	private void createAllTable() {
		jdbcTemplate.execute("create table department (id bigint auto_increment, name varchar(255), primary key (id))");
		jdbcTemplate.execute("create table Employee (id bigint auto_increment, name varchar(255), department_id bigint, FOREIGN KEY (department_id) REFERENCES department(id), primary key (id))");
	}
}
