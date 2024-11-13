package persistence.entity;

import fixture.EntityWithId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.manager.EntityEntry;
import persistence.entity.manager.EntityKey;
import persistence.entity.manager.EntityStatus;
import persistence.entity.manager.factory.PersistenceContext;
import persistence.meta.EntityTable;

import java.lang.reflect.Field;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class PersistenceContextTest {
    @Test
    @DisplayName("엔티티 저장소에 엔티티를 추가한다.")
    void addEntity() throws NoSuchFieldException, IllegalAccessException {
        // given
        final PersistenceContext persistenceContext = new PersistenceContext();
        final EntityWithId entity = new EntityWithId(1L, "Jaden", 30, "test@email.com");
        final EntityTable entityTable = new EntityTable(EntityWithId.class);

        // when
        persistenceContext.addEntity(entity, entityTable.getIdValue(entity));

        // then
        final Object managedEntity = getManagedEntity(persistenceContext, entity.getClass(), entity.getId());
        assertThat(managedEntity).isEqualTo(entity);
    }

    @Test
    @DisplayName("엔티티 저장소에서 엔티티를 반환한다.")
    void getEntity() {
        // given
        final PersistenceContext persistenceContext = new PersistenceContext();
        final EntityWithId entity = new EntityWithId(1L, "Jaden", 30, "test@email.com");
        final EntityTable entityTable = new EntityTable(EntityWithId.class);
        persistenceContext.addEntity(entity, entityTable.getIdValue(entity));

        // when
        final EntityWithId managedEntity = persistenceContext.getEntity(entity.getClass(), entity.getId());

        // then
        assertThat(managedEntity).isEqualTo(entity);
    }

    @Test
    @DisplayName("엔티티 저장소에서 엔티티를 제거한다.")
    void removeEntity() throws NoSuchFieldException, IllegalAccessException {
        // given
        final PersistenceContext persistenceContext = new PersistenceContext();
        final EntityWithId entity = new EntityWithId(1L, "Jaden", 30, "test@email.com");
        final EntityTable entityTable = new EntityTable(EntityWithId.class);
        persistenceContext.addEntity(entity, entityTable.getIdValue(entity));

        // when
        persistenceContext.removeEntity(entity, entityTable.getIdValue(entity));

        // then
        final Object managedEntity = getManagedEntity(persistenceContext, entity.getClass(), entity.getId());
        final EntityStatus entityStatus = getEntityStatus(persistenceContext, entity);
        assertAll(
                () -> assertThat(managedEntity).isNull(),
                () -> assertThat(entityStatus).isEqualTo(EntityStatus.GONE)
        );
    }

    private Object getManagedEntity(PersistenceContext persistenceContext, Class<?> clazz, Object id)
            throws NoSuchFieldException, IllegalAccessException {
        final Field field = persistenceContext.getClass().getDeclaredField("entityRegistry");
        field.setAccessible(true);
        final Map<EntityKey, Object> entityRegistry = (Map<EntityKey, Object>) field.get(persistenceContext);
        return entityRegistry.get(new EntityKey(clazz, id));
    }

    private EntityStatus getEntityStatus(PersistenceContext persistenceContext, Object entity)
            throws NoSuchFieldException, IllegalAccessException {
        final Field field = persistenceContext.getClass().getDeclaredField("entityEntryRegistry");
        field.setAccessible(true);
        final Map<Object, EntityEntry> entityEntryRegistry = (Map<Object, EntityEntry>) field.get(persistenceContext);
        return entityEntryRegistry.get(entity).getEntityStatus();
    }
}
