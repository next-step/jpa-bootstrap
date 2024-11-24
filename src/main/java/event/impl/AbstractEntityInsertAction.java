package event.impl;

import event.EntityAction;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import persistence.sql.dml.MetadataLoader;

import java.lang.reflect.Field;

public abstract class AbstractEntityInsertAction implements EntityAction {

    abstract boolean isDelayed();

    protected boolean isNotIdentityGenerationType(MetadataLoader<?> metadataLoader) {
        Field idField = metadataLoader.getPrimaryKeyField();
        GeneratedValue anno = idField.getAnnotation(GeneratedValue.class);

        return anno == null || anno.strategy() != GenerationType.IDENTITY;
    }
}
