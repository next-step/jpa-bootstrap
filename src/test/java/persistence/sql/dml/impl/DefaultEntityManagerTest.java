package persistence.sql.dml.impl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.TestEntityInitialize;
import persistence.config.TestPersistenceConfig;
import persistence.sql.context.PersistenceContext;
import persistence.sql.dml.EntityManager;
import persistence.sql.dml.EntityManagerFactory;
import persistence.sql.fixture.LazyTestOrder;
import persistence.sql.fixture.LazyTestOrderItem;
import persistence.sql.fixture.TestOrder;
import persistence.sql.fixture.TestOrderItem;
import persistence.sql.fixture.TestPerson;
import persistence.util.TestReflectionUtils;

import java.lang.reflect.Proxy;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("DefaultEntityManager 테스트")
class DefaultEntityManagerTest extends TestEntityInitialize {
    private EntityManager entityManager;
    private PersistenceContext persistenceContext;

    @BeforeEach
    void setUp() throws SQLException {
        TestPersistenceConfig config = TestPersistenceConfig.getInstance();
        EntityManagerFactory factory = config.entityManagerFactory();
        entityManager = factory.entityManager();
        persistenceContext = TestReflectionUtils.getFieldValue(entityManager, "persistenceContext");
    }

    @Test
    @DisplayName("find 함수는 식별자가 유효한 경우 적절한 엔티티를 조회한다.")
    void testFind() {
        // given
        TestPerson person = new TestPerson("catsbi", 55, "catsbi@naver.com", 123);
        entityManager.persist(person);

        // when
        TestPerson actual = entityManager.find(TestPerson.class, 1L);

        assertAll(
                () -> assertThat(actual).isNotNull(),
                () -> assertThat(actual.getName()).isEqualTo("catsbi"),
                () -> assertThat(actual.getAge()).isEqualTo(55),
                () -> assertThat(actual.getEmail()).isEqualTo("catsbi@naver.com"),
                () -> assertThat(actual.getIndex()).isNull()
        );
    }

    @Test
    @DisplayName("find 함수는 식별자가 유효하지 않은 경우 null을 반환한다.")
    void testFindWithInvalidId() {
        // given
        TestPerson person = new TestPerson("catsbi", 55, "catsbi@naver.com", 123);
        entityManager.persist(person);

        // when
        TestPerson actual = entityManager.find(TestPerson.class, 999L);
        assertThat(actual).isNull();
    }

    @Test
    @DisplayName("find 함수는 식별자를 전달하지 않을 경우 예외를 던진다.")
    void testFindWithNullId() {
        // when, then
        assertThatThrownBy(() -> entityManager.find(TestPerson.class, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Primary key must not be null");
    }

    @Test
    @DisplayName("persist 함수는 엔티티를 저장한다.")
    void testPersist() {
        // given
        TestPerson person = new TestPerson("catsbi", 55, "catsbi@naver.com", 123);

        // when
        entityManager.persist(person);

        // then
        TestPerson actual = entityManager.find(TestPerson.class, 1L);

        assertThat(actual).isNotNull();
    }

    @Test
    @DisplayName("persist 함수는 엔티티 매개변수를 전달하지 않을 경우 예외를 던진다.")
    void testPersistWithNullEntity() {
        // when, then
        assertThatThrownBy(() -> entityManager.persist(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Entity must not be null");
    }

    @Test
    @DisplayName("merge 함수는 식별자가 이미 존재하는 Row의 식별자인 경우 엔티티를 병합한다.")
    void testMerge() {
        // given
        TestPerson person = new TestPerson("catsbi", 55, "casbi@naver.com", 123);
        entityManager.persist(person);

        // when
        TestPerson newPerson = new TestPerson(1L, "hansol", 33, "hansol@naver.com", 123);
        entityManager.merge(newPerson);

        // then
        TestPerson mergedPerson = entityManager.find(TestPerson.class, 1L);
        assertAll(
                () -> assertThat(mergedPerson).isNotNull(),
                () -> assertThat(mergedPerson.getName()).isEqualTo("hansol"),
                () -> assertThat(mergedPerson.getAge()).isEqualTo(33),
                () -> assertThat(mergedPerson.getEmail()).isEqualTo("hansol@naver.com")

        );
    }

    @Test
    @DisplayName("merge 함수는 식별자가 존재하지 않는 Row의 식별자인 경우 엔티티를 저장한다.")
    void testMergeWithNewEntity() {
        // given
        TestPerson person = new TestPerson("catsbi", 55, "casbi@naver.com", 123);
        entityManager.persist(person);

        TestPerson foundPerson = entityManager.find(TestPerson.class, 1L);
        foundPerson.setId(2L);
        entityManager.merge(foundPerson);

        List<TestPerson> foundPersons = entityManager.findAll(TestPerson.class);
        assertThat(foundPersons).hasSize(2);
    }

    @Test
    @DisplayName("remove 함수는 엔티티를 삭제한다.")
    void testRemove() {
        // given
        TestPerson person = new TestPerson("catsbi", 55, "casbi@naver.com", 123);

        // when
        entityManager.persist(person);
        List<TestPerson> persons = entityManager.findAll(TestPerson.class);

        assertThat(persons).hasSize(1);

        entityManager.remove(persons.getFirst());

        persons = entityManager.findAll(TestPerson.class);
        assertThat(persons).isEmpty();
    }

    @Test
    @DisplayName("transaction 상태를 on으로 바꾼 경우 dirty checking이 활성화된다.")
    void testDirtyChecking() {
        // given
        TestPerson person = new TestPerson("catsbi", 55, "casbi@naver.com", 123);
        entityManager.persist(person);

        // when
        entityManager.getTransaction().begin();
        TestPerson foundPerson = entityManager.find(TestPerson.class, 1L);
        foundPerson.setName("newCatsbi");
        entityManager.getTransaction().commit();

        //given
        TestPerson loadedEntity = entityManager.find(TestPerson.class, 1L);
        assertThat(loadedEntity.getName()).isEqualTo("newCatsbi");
    }

    @Test
    @DisplayName("persist 함수는 연관관계 엔티티가 있고 영속성 전이 Persis 전략인 경우 경우 연관관계 엔티티도 함께 저장한다.")
    void testPersistWithCascadePersist() {
        // given
        TestOrder testOrder = new TestOrder("order1");
        TestOrderItem apple = new TestOrderItem("apple", 10);
        TestOrderItem grape = new TestOrderItem("grape", 20);
        testOrder.addOrderItem(apple);
        testOrder.addOrderItem(grape);

        entityManager.getTransaction().begin();
        entityManager.persist(testOrder);
        entityManager.getTransaction().commit();

        // when
        TestOrder actual = entityManager.find(TestOrder.class, testOrder.getId());

        // then
        assertAll(
                () -> assertThat(actual).isNotNull(),
                () -> assertThat(actual.getOrderItems()).hasSize(2),
                () -> assertThat(actual.getOrderItems()).containsAll(List.of(apple, grape))
        );
    }

    @Test
    @DisplayName("find 조회시 지연로딩 프록시 객체는 트랜잭셔널 상태일 경우 지연로딩을 제공한다.")
    void testFindWithLazyLoading() {
        // given
        LazyTestOrder testOrder = new LazyTestOrder("order1");
        LazyTestOrderItem apple = new LazyTestOrderItem("apple", 10);
        LazyTestOrderItem grape = new LazyTestOrderItem("grape", 20);
        testOrder.addOrderItem(apple);
        testOrder.addOrderItem(grape);

        entityManager.persist(testOrder);
        TestReflectionUtils.setFieldValue(persistenceContext, "context", new HashMap<>());
        TestReflectionUtils.setFieldValue(persistenceContext, "collectionContext", new HashMap<>());

        entityManager.getTransaction().begin();
        // when
        LazyTestOrder lazyTestOrder = entityManager.find(LazyTestOrder.class, testOrder.getId());
        List<LazyTestOrderItem> actual = lazyTestOrder.getOrderItems();

        // then
        assertAll(
                () -> assertThat(actual).isInstanceOf(Proxy.class),
                () -> assertThat(actual).isNotNull(),
                () -> assertThat(actual.size()).isEqualTo(0)
        );

        entityManager.getTransaction().commit();
    }

    @Test
    @DisplayName("find 조회시 지연로딩 프록시 객체는 지연로딩 이후 실제 값을 반환한다.")
    void testFindWithLazyLoadingWithRealize() {
        // given
        LazyTestOrder testOrder = new LazyTestOrder("order1");
        LazyTestOrderItem apple = new LazyTestOrderItem("apple", 10);
        LazyTestOrderItem grape = new LazyTestOrderItem("grape", 20);
        testOrder.addOrderItem(apple);
        testOrder.addOrderItem(grape);

        entityManager.persist(testOrder);
        TestReflectionUtils.setFieldValue(persistenceContext, "context", new HashMap<>());
        TestReflectionUtils.setFieldValue(persistenceContext, "collectionContext", new HashMap<>());

        entityManager.getTransaction().begin();
        // when
        LazyTestOrder lazyTestOrder = entityManager.find(LazyTestOrder.class, testOrder.getId());
        List<LazyTestOrderItem> actual = lazyTestOrder.getOrderItems();
        actual.iterator();

        // then
        assertAll(
                () -> assertThat(actual).isInstanceOf(Proxy.class),
                () -> assertThat(actual).containsAll(List.of(apple, grape)),
                () -> assertThat(actual.size()).isEqualTo(2)
        );

        entityManager.getTransaction().commit();
    }

    @Test
    @DisplayName("find 조회시 지연로딩 프록시 객체는 트랜잭셔널 상태가 아닐 경우 지연로딩을 제공하지 않고 예외를 던진다.")
    void testFindWithLazyLoadingNoTransactional() {
        // given
        LazyTestOrder testOrder = new LazyTestOrder("order1");
        LazyTestOrderItem apple = new LazyTestOrderItem("apple", 10);
        LazyTestOrderItem grape = new LazyTestOrderItem("grape", 20);
        testOrder.addOrderItem(apple);
        testOrder.addOrderItem(grape);

        entityManager.persist(testOrder);
        TestReflectionUtils.setFieldValue(persistenceContext, "context", new HashMap<>());
        TestReflectionUtils.setFieldValue(persistenceContext, "collectionContext", new HashMap<>());

        // when
        LazyTestOrder actual = entityManager.find(LazyTestOrder.class, testOrder.getId());

        // then
        Assertions.assertThatThrownBy(() -> actual.getOrderItems().iterator())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("failed to lazily initialize a collection");
    }
}
