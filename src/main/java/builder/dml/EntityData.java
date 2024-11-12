package builder.dml;

import java.util.List;
import java.util.Map;

public class EntityData {

    private EntityMetaData entityMetaData;
    private EntityObjectData entityObjectData;

    public EntityData(EntityMetaData entityMetaData, EntityObjectData entityObjectData) {
        this.entityMetaData = entityMetaData;
        this.entityObjectData = entityObjectData;
    }

    public EntityData(EntityMetaData entityMetaData) {
        this.entityMetaData = entityMetaData;
    }

    public EntityMetaData getEntityMetaData() {
        return entityMetaData;
    }

    public EntityObjectData getEntityObjectData() {
        return entityObjectData;
    }

    public Object getId() {
        return this.entityObjectData.getId();
    }

    public Object getEntityInstance() {
        return this.entityObjectData.getEntityInstance();
    }

    public EntityColumn getEntityColumn() {
        return this.entityObjectData.getEntityColumn();
    }

    public JoinEntity getJoinEntity() {
        return this.entityObjectData.getJoinEntity();
    }

    public String wrapString() {
        return this.entityObjectData.wrapString();
    }

    public EntityData changeColumns(List<DMLColumnData> columns) {
        this.entityObjectData = this.entityObjectData.changeColumns(columns);
        return this;
    }

    public String getColumnDefinitions() {
        return this.entityObjectData.getColumnDefinitions();
    }

    public List<DMLColumnData> getDifferentColumns(EntityData snapshotEntityData) {
        return this.entityObjectData.getDifferentColumns(snapshotEntityData);
    }

    public Map<String, DMLColumnData> convertDMLColumnDataMap() {
        return this.entityObjectData.convertDMLColumnDataMap();
    }

    public boolean checkJoin() {
        return this.entityObjectData.checkJoin();
    }

    public boolean checkJoinAndEager() {
        return this.entityObjectData.checkJoinAndEager();
    }

    public String getTableName() {
        return this.entityMetaData.getTableName();
    }

    public Class<?> getClazz() {
        return this.entityMetaData.getClazz();
    }

    public String getPkNm() {
        return this.entityMetaData.getPkName();
    }

    public String getAlias() {
        return this.entityMetaData.getAlias();
    }
}
