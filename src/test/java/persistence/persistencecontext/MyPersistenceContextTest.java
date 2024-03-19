package persistence.persistencecontext;

import domain.Person;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MyPersistenceContextTest {

    @Test
    void addEntity() {
        //given
        MyPersistenceContext persistenceContext = new MyPersistenceContext();
        Person person = new Person(1L, "ABC", 10, "ABC@email.com", 10);

        //when
        persistenceContext.addEntity(1L, person);

        //then
        assertThat(persistenceContext.getEntity(Person.class, 1L)).isNotNull();
    }

    @Test
    void getEntity() {
        //given
        MyPersistenceContext persistenceContext = new MyPersistenceContext();
        String expectedName = "ABC";
        Person person = new Person(1L, expectedName, 10, "ABC@email.com", 10);
        persistenceContext.addEntity(1L, person);

        //when
        Object actual = persistenceContext.getEntity(Person.class, 1L).get();

        //then
        assertThat(actual).extracting("name").isEqualTo(expectedName);
    }

    @Test
    void removeEntity() {
        //given
        MyPersistenceContext persistenceContext = new MyPersistenceContext();
        Person person = new Person(1L, "ABC", 10, "ABC@email.com", 10);
        persistenceContext.addEntity(1L, person);

        //when
        persistenceContext.removeEntity(1L, person);

        //then
        assertThat(persistenceContext.getEntity(Person.class, 1L).isEmpty()).isTrue();
    }

    @Test
    void getDatabaseSnapshot() {
        //given
        MyPersistenceContext persistenceContext = new MyPersistenceContext();
        Person person = new Person(1L, "ABC", 10, "ABC@email.com", 10);
        persistenceContext.getDatabaseSnapshot(1L, person);

        //when
        EntitySnapshot actual = persistenceContext.getDatabaseSnapshot(1L, person);

        //then
        assertThat(actual.getSnapshot()).isNotEmpty();
    }
}
