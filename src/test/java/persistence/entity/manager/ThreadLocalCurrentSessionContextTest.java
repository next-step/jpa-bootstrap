package persistence.entity.manager;

import mock.MockDmlGenerator;
import mock.MockJdbcTemplate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.Application;
import persistence.core.EntityMetadataProvider;
import persistence.core.EntityScanner;
import persistence.core.MetaModel;
import persistence.core.MetaModelImpl;
import persistence.entity.loader.EntityLoaders;
import persistence.entity.persister.EntityPersisters;
import persistence.exception.CurrentSessionAlreadyOpenException;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class ThreadLocalCurrentSessionContextTest {

    private static MetaModel metaModel;

    @BeforeAll
    static void beforeAll() {
        final EntityMetadataProvider entityMetadataProvider = EntityMetadataProvider.from(new EntityScanner(Application.class));
        final MockDmlGenerator dmlGenerator = new MockDmlGenerator();
        final MockJdbcTemplate jdbcTemplate = new MockJdbcTemplate();
        final EntityPersisters entityPersisters = new EntityPersisters(entityMetadataProvider, dmlGenerator, jdbcTemplate);
        final EntityLoaders entityLoaders = new EntityLoaders(entityMetadataProvider, dmlGenerator, jdbcTemplate);
        metaModel = new MetaModelImpl(entityMetadataProvider, entityPersisters, entityLoaders);
    }

    @Test
    @DisplayName("currentSessionContext.open 을 통해 현재 Thread 의 Session 정보를 저장 할 수 있다.")
    void currentSessionContextOpenTest() {
        final ThreadLocalCurrentSessionContext currentSessionContext = new ThreadLocalCurrentSessionContext();

        final SimpleEntityManager entityManager = new SimpleEntityManager(metaModel, currentSessionContext);
        currentSessionContext.open(entityManager);

        final EntityManager currentSession = currentSessionContext.getCurrentSession().get();

        assertThat(currentSession == entityManager).isTrue();
    }

    @Test
    @DisplayName("이미 열린 Session 이 있다면 currentSessionContext.open 재호출시 Exception 이 던져진다.")
    void currentSessionContextAlreadyOpenTest() {
        final ThreadLocalCurrentSessionContext currentSessionContext = new ThreadLocalCurrentSessionContext();
        final SimpleEntityManager firstEntityManager = new SimpleEntityManager(metaModel, currentSessionContext);

        currentSessionContext.open(firstEntityManager);

        assertThatThrownBy(() -> {
            final SimpleEntityManager secondEntityManager = new SimpleEntityManager(metaModel, currentSessionContext);
            currentSessionContext.open(secondEntityManager);
        }).isInstanceOf(CurrentSessionAlreadyOpenException.class);
    }

    @Test
    @DisplayName("열린 Session 이 없다면 getCurrentSession 호출시 Optional.empty 가 반환된다.")
    void noCurrentSessionContextOpenTest() {
        final CurrentSessionContext currentSessionContext = new ThreadLocalCurrentSessionContext();

        assertThat(currentSessionContext.getCurrentSession()).isEqualTo(Optional.empty());
    }

    @Test
    @DisplayName("서로 다른 Thread 에서 만든 entityManager 는 CurrentSessionContext 안에서 서로 독립된 객체로 바라본다.")
    void currentSessionContextOpenWithOtherThreadsTest() throws InterruptedException {
        final ThreadLocalCurrentSessionContext currentSessionContext = new ThreadLocalCurrentSessionContext();
        final Map<Thread, EntityManager> entityManagerMap = new ConcurrentHashMap<>();

        final Thread firstThread = openCurrentSessionInAnotherThread(currentSessionContext, entityManagerMap);
        final Thread secondThread = openCurrentSessionInAnotherThread(currentSessionContext, entityManagerMap);
        firstThread.start();
        secondThread.start();
        openSessionContext(currentSessionContext, entityManagerMap);
        firstThread.join();
        secondThread.join();

        assertSoftly(softly->{
            softly.assertThat(entityManagerMap.get(firstThread)).isNotEqualTo(entityManagerMap.get(secondThread));
            softly.assertThat(entityManagerMap.get(firstThread)).isNotEqualTo(entityManagerMap.get(Thread.currentThread()));
            softly.assertThat(entityManagerMap.get(secondThread)).isNotEqualTo(entityManagerMap.get(Thread.currentThread()));
        });
    }

    @Test
    @DisplayName("현재 Thread 에 열린 Session 을 close 를 통해 지울 수 있다.")
    void currentSessionContextCloseTest() {
        final ThreadLocalCurrentSessionContext currentSessionContext = new ThreadLocalCurrentSessionContext();
        final SimpleEntityManager firstEntityManager = new SimpleEntityManager(metaModel, currentSessionContext);
        currentSessionContext.open(firstEntityManager);

        currentSessionContext.close();

        assertThat(currentSessionContext.getCurrentSession()).isEqualTo(Optional.empty());
    }


    private static void openSessionContext(final ThreadLocalCurrentSessionContext currentSessionContext, final Map<Thread, EntityManager> entityManagerMap) {
        final SimpleEntityManager entityManager = new SimpleEntityManager(metaModel, currentSessionContext);
        currentSessionContext.open(entityManager);
        final EntityManager currentSession = currentSessionContext.getCurrentSession().get();
        entityManagerMap.put(Thread.currentThread(), currentSession);
    }

    private static Thread openCurrentSessionInAnotherThread(final ThreadLocalCurrentSessionContext currentSessionContext, final Map<Thread, EntityManager> entityManagerMap) {
        return new Thread(() -> openSessionContext(currentSessionContext, entityManagerMap));
    }
}
