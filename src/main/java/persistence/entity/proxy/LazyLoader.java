package persistence.entity.proxy;

import persistence.entity.loader.CollectionLoader;
import persistence.meta.EntityTable;

import java.util.List;

public class LazyLoader {
    public static final String NO_ONE_TO_ONE_LAZY_FAILED_MESSAGE = "@OneToMany 연관관계이면서 LAZY 타입인 컬럼이 존재하지 않습니다.";

    private final EntityTable parentEntityTable;
    private final CollectionLoader collectionLoader;

    public LazyLoader(EntityTable parentEntityTable, CollectionLoader collectionLoader) {
        this.parentEntityTable = parentEntityTable;
        this.collectionLoader = collectionLoader;
        validate();
    }

    public List<?> load(Object parentEntity) {
        return collectionLoader.load(parentEntityTable.getAssociationCondition(parentEntity));
    }

    private void validate() {
        if (!parentEntityTable.isOneToMany() || parentEntityTable.isEager()) {
            throw new IllegalArgumentException(NO_ONE_TO_ONE_LAZY_FAILED_MESSAGE);
        }
    }
}