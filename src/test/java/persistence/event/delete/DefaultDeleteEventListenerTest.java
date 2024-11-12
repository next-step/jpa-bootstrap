package persistence.event.delete;

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

class DefaultDeleteEventListenerTest {

    @Test
    void testOnDelete() throws Exception {

        DatabaseServer databaseServer = new H2();
        Metadata metadata = new MetadataImpl(databaseServer);

        SessionFactoryImpl sessionFactory = new SessionFactoryImpl(new ThreadLocalCurrentSessionContext(), metadata);

        EventSource source = (EventSource) sessionFactory.openSession();
        DefaultDeleteEventListener defaultDeleteEventListener = new DefaultDeleteEventListener();

        SimplePerson entity = new SimplePerson(1L, "John");
        EntityKey entityKey = new EntityKey(1L, SimplePerson.class);

        EntityEntry entityEntry = new EntityEntry(Status.MANAGED, 1L);

        source.getPersistenceContext().addEntity(entityKey, entity);
        source.getPersistenceContext().addDatabaseSnapshot(entityKey, entity, source.findEntityPersister(SimplePerson.class));
        source.getPersistenceContext().addEntry(entityKey, entityEntry);

        DeleteEvent deleteEvent = DeleteEvent.create(source, entity, entityEntry);
        defaultDeleteEventListener.onDelete(deleteEvent);

        assertAll(
                () -> assertThat(source.getPersistenceContext().getEntity(entityKey)).isEqualTo(entity),
                () -> assertThat(source.getPersistenceContext().getDatabaseSnapshot(entityKey)).isNotNull(),
                () -> assertThat(source.getPersistenceContext().getEntityEntry(entityKey).isManaged()).isFalse()
        );

        sessionFactory.close();
    }
}
