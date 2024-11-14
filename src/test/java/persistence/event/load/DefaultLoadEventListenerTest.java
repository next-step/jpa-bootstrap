package persistence.event.load;

import database.DatabaseServer;
import database.H2;
import org.junit.jupiter.api.Test;
import persistence.entity.EntityEntry;
import persistence.entity.Status;
import persistence.event.EventSource;
import persistence.fixtures.SimplePerson;
import persistence.meta.Metadata;
import persistence.meta.MetadataImpl;
import persistence.session.SessionFactoryImpl;
import persistence.session.ThreadLocalCurrentSessionContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class DefaultLoadEventListenerTest {

    @Test
    void testOnLoad() throws Exception {
        DatabaseServer databaseServer = new H2();
        Metadata metadata = new MetadataImpl(databaseServer);

        SessionFactoryImpl sessionFactory = new SessionFactoryImpl(new ThreadLocalCurrentSessionContext(), metadata);
        EventSource source = (EventSource) sessionFactory.openSession();

        // for test
        SimplePerson entity = new SimplePerson(1L, "John");
        source.findEntityPersister(SimplePerson.class).insert(entity);

        DefaultLoadEventListener defaultLoadEventListener = new DefaultLoadEventListener();

        LoadEvent<SimplePerson> loadEvent = new LoadEvent<>(source, SimplePerson.class, 1L, new EntityEntry(
                Status.MANAGED, 1L
        ));
        defaultLoadEventListener.onLoad(loadEvent);

        assertAll(
                () -> assertThat(loadEvent.getResultEntity().getId()).isEqualTo(1L),
                () -> assertThat(loadEvent.getResultEntity().getName()).isEqualTo("John")
        );
        sessionFactory.close();
    }
}
