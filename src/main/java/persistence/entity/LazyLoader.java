package persistence.entity;

import persistence.meta.EntityTable;

import java.util.List;

public class LazyLoader<T> {
    public static final String NO_ONE_TO_ONE_LAZY_FAILED_MESSAGE = "@OneToMany 연관관계이면서 LAZY 타입인 컬럼이 존재하지 않습니다.";

    private final Class<T> entityType;
    private final EntityTable parentEntityTable;
    private final CollectionLoader collectionLoader;

    public LazyLoader(Class<T> entityType, Object parentEntity, CollectionLoader collectionLoader) {
        this.entityType = entityType;
        this.parentEntityTable = new EntityTable(parentEntity);
        this.collectionLoader = collectionLoader;
        validate();
    }

    public List<T> load() {
        return collectionLoader.load(entityType, parentEntityTable.getAssociationColumnName(), parentEntityTable.getIdValue());
    }

    private void validate() {
        if (!parentEntityTable.isOneToMany() || parentEntityTable.isEager()) {
            throw new IllegalArgumentException(NO_ONE_TO_ONE_LAZY_FAILED_MESSAGE);
        }
    }
}
