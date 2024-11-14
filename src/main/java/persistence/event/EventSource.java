package persistence.event;

import persistence.action.ActionQueue;
import persistence.entity.CollectionPersister;
import persistence.entity.EntityLoader;
import persistence.entity.EntityPersister;
import persistence.entity.PersistenceContext;
import persistence.session.EntityManager;
import persistence.sql.definition.TableAssociationDefinition;

public interface EventSource extends EntityManager {

    ActionQueue getActionQueue();

    PersistenceContext getPersistenceContext();

    EntityPersister findEntityPersister(Class<?> clazz);

    CollectionPersister findCollectionPersister(TableAssociationDefinition association);

    EntityLoader findEntityLoader(Class<?> clazz);
}
