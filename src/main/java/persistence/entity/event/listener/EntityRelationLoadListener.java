package persistence.entity.event.listener;

import persistence.entity.event.EntityEvent;
import persistence.entity.event.RelationEntityEvent;
import persistence.entity.event.action.ActionQueue;
import persistence.entity.loader.EntityLoader;
import persistence.sql.meta.MetaModel;

public class EntityRelationLoadListener extends AbstractEntityListener implements EntityListener {

    public EntityRelationLoadListener(MetaModel metaModel) {
        super(metaModel);
    }

    @Override
    public <T> Object handleEvent(EntityEvent<T> event, ActionQueue actionQueue) {
        if (event instanceof RelationEntityEvent) {
            RelationEntityEvent<T> relationEntityEvent = (RelationEntityEvent<T>) event;
            EntityLoader<T> entityLoader = metaModel.getEntityLoader(event.getEntityClass());
            return entityLoader.find(relationEntityEvent.getWhere());
        }
        throw new IllegalArgumentException("올바르지 않은 이벤트 입니다.");
    }
}
