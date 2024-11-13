package builder.dml;

import util.StringUtil;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EntityObjectData {

    private final EntityColumn entityColumn;
    private final JoinEntity joinEntity;
    private final Class<?> clazz;
    private Object id;
    private Object entityInstance;

    public EntityObjectData(Object entityInstance) {
        this.clazz = entityInstance.getClass();
        this.entityColumn = new EntityColumn(entityInstance, this.clazz);
        this.id = this.entityColumn.getPkValue();
        this.entityInstance = deepCopy(entityInstance);
        this.joinEntity = new JoinEntity(entityInstance, this.id);
    }

    public EntityObjectData(Class<?> clazz) {
        this.clazz = clazz;
        this.entityColumn = new EntityColumn(this.clazz);
        this.joinEntity = new JoinEntity(this.clazz, this.id);
    }

    public EntityObjectData(Class<?> clazz, Object id) {
        this.clazz = clazz;
        this.id = id;
        this.entityColumn = new EntityColumn(this.clazz);
        this.joinEntity = new JoinEntity(this.clazz, this.id);
    }

    public String wrapString() {
        return (this.id instanceof String) ? StringUtil.wrapSingleQuote(this.id) : String.valueOf(this.id);
    }

    public EntityObjectData changeColumns(List<DMLColumnData> columns) {
        this.entityColumn.changeColumns(columns);
        return this;
    }

    public String getColumnDefinitions() {
        return this.entityColumn.getColumnDefinitions();
    }

    public List<DMLColumnData> getDifferentColumns(EntityData snapshotEntityData) {
        return this.entityColumn.getDifferentColumns(snapshotEntityData);
    }

    public Map<String, DMLColumnData> convertDMLColumnDataMap() {
        return this.entityColumn.getColumns().stream()
                .collect(Collectors.toMap(DMLColumnData::getColumnName, Function.identity()));
    }

    public boolean checkJoin() {
        return this.joinEntity.checkJoin();
    }

    public boolean checkJoinAndEager() {
        return this.joinEntity.checkJoin() && this.joinEntity.checkFetchEager();
    }

    public Object getId() {
        return id;
    }

    public EntityColumn getEntityColumn() {
        return entityColumn;
    }

    public Object getEntityInstance() {
        return entityInstance;
    }

    public JoinEntity getJoinEntity() {
        return joinEntity;
    }

    private Object deepCopy(Object original) {
        if (original == null) return null;

        try {
            Class<?> clazz = original.getClass();
            Object copy = clazz.getDeclaredConstructor().newInstance();

            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);

                Object value = field.get(original);
                field.set(copy, value);
            }
            return copy;
        } catch (Exception e) {
            throw new RuntimeException("Deep copy failed", e);
        }
    }
}
