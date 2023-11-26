package registry;

import persistence.sql.dialect.ColumnType;
import persistence.sql.exception.EntityMappingException;
import persistence.sql.schema.meta.EntityClassMappingMeta;
import registry.context.MetaModelContext;

public class EntityMetaRegistry {

    private final MetaModelContext metaModelContext;
    private final ColumnType columnType;

    private EntityMetaRegistry(ColumnType columnType) {
        this.metaModelContext = new MetaModelContext();
        this.columnType = columnType;
    }

    public static EntityMetaRegistry of(ColumnType columnType) {
        return new EntityMetaRegistry(columnType);
    }

    public void addEntityMeta(Class<?> clazz) {
        final EntityClassMappingMeta entityClassMappingMeta = EntityClassMappingMeta.of(clazz, columnType);
        this.metaModelContext.putEntityMeta(clazz, entityClassMappingMeta);
    }

    public EntityClassMappingMeta getEntityMeta(Class<?> clazz) {
        final EntityClassMappingMeta entityClassMappingMeta = this.metaModelContext.getEntityMeta(clazz);

        if (entityClassMappingMeta == null) {
            throw EntityMappingException.preconditionRequired("Entity 등록");
        }

        return entityClassMappingMeta;
    }
}
