package persistence.meta;

import jakarta.persistence.Transient;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EntityColumns {
    public static final String NOT_ID_FAILED_MESSAGE = "필드에 @Id 애노테이션이 없습니다.";

    private final List<EntityColumn> entityColumns;

    public EntityColumns(Class<?> entityType) {
        this.entityColumns = Arrays.stream(entityType.getDeclaredFields())
                .filter(this::isPersistent)
                .map(EntityColumn::new)
                .collect(Collectors.toList());
    }

    public List<EntityColumn> getEntityColumns() {
        return entityColumns;
    }

    public void setValue(Object entity) {
        Arrays.stream(entity.getClass().getDeclaredFields())
                .filter(this::isPersistent)
                .forEach(field -> {
                    final EntityColumn entityColumn = findEntityColumn(field);
                    entityColumn.setValue(field, entity);
                });
    }

    private EntityColumn findEntityColumn(Field field) {
        return entityColumns.stream()
                .filter(entityColumn -> entityColumn.getField().equals(field))
                .findFirst()
                .orElseThrow();
    }

    public EntityColumn getIdEntityColumn() {
        return entityColumns.stream()
                .filter(EntityColumn::isId)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(NOT_ID_FAILED_MESSAGE));
    }

    private boolean isPersistent(Field field) {
        return !field.isAnnotationPresent(Transient.class);
    }

    public EntityColumn getAssociationEntityColumn() {
        return entityColumns.stream()
                .filter(EntityColumn::isOneToMany)
                .findFirst()
                .orElse(null);
    }
}
