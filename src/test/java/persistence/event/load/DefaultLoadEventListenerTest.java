package persistence.event.load;

import database.DatabaseServer;
import database.H2;
import org.junit.jupiter.api.Test;
import persistence.entity.EntityEntry;
import persistence.entity.EntityKey;
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
        DefaultLoadEventListener defaultLoadEventListener = new DefaultLoadEventListener();

        SimplePerson entity = new SimplePerson(1L, "John");
        LoadEvent loadEvent = LoadEvent.create(source, 1L, entity, new EntityEntry(
                Status.MANAGED, 1L
        ));
        defaultLoadEventListener.onLoad(loadEvent);

        EntityKey entityKey = new EntityKey(1L, SimplePerson.class);
        assertAll(
                () -> assertThat(source.getPersistenceContext().getEntity(entityKey)).isEqualTo(entity),
                () -> assertThat(source.getPersistenceContext().getDatabaseSnapshot(entityKey)).isNotNull(),
                () -> assertThat(source.getPersistenceContext().getEntityEntry(entityKey).isManaged()).isTrue()
        );

        sessionFactory.close();
    }
}
