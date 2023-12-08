package persistence.meta.relation;

import jakarta.persistence.FetchType;
import persistence.meta.MetaEntity;

public class MetaDataColumnEmptyRelation implements Relation {

  public MetaDataColumnEmptyRelation() {
  }

  @Override
  public boolean isRelation() {
    return false;
  }

  @Override
  public String getDbName() {
    throw new IllegalStateException("relation이 존재하지 않는 Column 입니다.");
  }

  @Override
  public FetchType getFetchType() {
    throw new IllegalStateException("relation이 존재하지 않는 Column 입니다.");
  }

  @Override
  public MetaEntity<?> getMetaEntity() {
    throw new IllegalStateException("relation이 존재하지 않는 Column 입니다.");
  }

  @Override
  public Class<?> getRelation() {
    throw new IllegalStateException("relation이 존재하지 않는 Column 입니다.");
  }

  @Override
  public String getFieldName() {
    throw new IllegalStateException("relation이 존재하지 않는 Column 입니다.");
  }

  @Override
  public Class<?> getCollectionType() {
    throw new IllegalStateException("relation이 존재하지 않는 Column 입니다.");
  }
}
