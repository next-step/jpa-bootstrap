package persistence.actionqueue;

import persistence.entity.attribute.id.IdAttribute;
import persistence.entity.persister.EntityPersister;

public class EntityInsertAction extends EntityAction {
    public EntityInsertAction(String entityName, IdAttribute idAttribute, String idValue,
                              Object instance, EntityPersister persister, Object snapshot) {
        super(entityName, idAttribute, idValue, instance, persister, snapshot);
    }

    @Override
    public void execute() {
        super.persister.insert(super.instance);
    }
}
