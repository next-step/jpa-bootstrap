package persistence.actionqueue;

import persistence.entity.attribute.id.IdAttribute;
import persistence.entity.persister.EntityPersister;

public class EntityUpdateAction extends EntityAction {

    public EntityUpdateAction(String tableName, IdAttribute idAttribute, String idValue,
                              Object instance, EntityPersister persister, Object snapshot) {
        super(tableName, idAttribute, idValue, instance, persister, snapshot);
    }

    @Override
    public void execute() {
        persister.update(snapshot, instance);
    }
}
