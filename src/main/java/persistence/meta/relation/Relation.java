package persistence.meta.relation;

import jakarta.persistence.FetchType;
import persistence.meta.MetaEntity;

public interface Relation {

  boolean isRelation();

  String getDbName();

  FetchType getFetchType();

  MetaEntity<?> getMetaEntity();

  Class<?> getRelation();

  String getFieldName();
}
