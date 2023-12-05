package registry;

import persistence.sql.dialect.Dialect;
import persistence.sql.exception.EntityMappingException;
import persistence.sql.schema.meta.EntityClassMappingMeta;
import registry.context.MetaModelContext;

public class EntityMetaRegistry {

    private final MetaModelContext metaModelContext;
    private final Dialect dialect;

    private EntityMetaRegistry(Dialect dialect) {
        this.metaModelContext = new MetaModelContext();
        this.dialect = dialect;
    }

    public static EntityMetaRegistry of(Dialect dialect) {
        return new EntityMetaRegistry(dialect);
    }

    public void addEntityMeta(Class<?> clazz) {
        final EntityClassMappingMeta entityClassMappingMeta = EntityClassMappingMeta.of(clazz, dialect);
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
