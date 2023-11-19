package persistence.actionqueue;

import persistence.entity.attribute.id.IdAttribute;
import persistence.entity.persister.EntityPersister;

public abstract class EntityAction {
    protected final Object instance;
    protected final Object snapshot;
    protected final EntityPersister persister;
    private final String tableName;
    private final String idValue;
    private final IdAttribute idAttribute;

    public EntityAction(String tableName, IdAttribute idAttribute, String idValue,
                        Object instance, EntityPersister persister, Object snapshot) {
        this.tableName = tableName;
        this.idAttribute = idAttribute;
        this.idValue = idValue;
        this.instance = instance;
        this.persister = persister;
        this.snapshot = snapshot;
    }

    public abstract void execute();
}
