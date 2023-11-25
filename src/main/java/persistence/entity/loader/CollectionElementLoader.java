package persistence.entity.loader;

import jakarta.persistence.FetchType;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import jdbc.CollectionRowMapper;
import jdbc.ElementRowMapper;
import jdbc.JdbcRowMapper;
import jdbc.JdbcTemplate;
import jdbc.RowMapper;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.LazyLoader;
import persistence.meta.MetaDataColumn;
import persistence.meta.MetaEntity;
import persistence.meta.relation.Relation;
import persistence.sql.dml.builder.read.JoinQueryBuilder;
import persistence.sql.dml.builder.read.SelectQueryBuilder;

public class CollectionElementLoader<T> implements RelationLoader<T> {

  private final JdbcTemplate jdbcTemplate;
  private final SelectQueryBuilder selectQueryBuilder = new SelectQueryBuilder();
  private final MetaEntity<T> metaEntity;
  private final RowMapper<T> rowMapper;
  private final MetaEntity<?> elementEntity;
  private final CollectionRowMapper<?> elementRowMapper;
  private final Relation relation;

  private CollectionElementLoader(JdbcTemplate jdbcTemplate, MetaEntity<T> metaEntity,
      MetaEntity<?> elementEntity, Relation relation) {
    this.jdbcTemplate = jdbcTemplate;
    this.metaEntity = metaEntity;
    this.elementEntity = elementEntity;
    this.rowMapper = new JdbcRowMapper<>(metaEntity);
    this.elementRowMapper = new ElementRowMapper<>(elementEntity);
    this.relation = relation;
  }

  public static <T> RelationLoader<?> of(MetaEntity<T> metaEntity, JdbcTemplate jdbcTemplate) {

    Relation relation = metaEntity.getRelation();

    if (!metaEntity.hasRelation()){
      return new EmptyCollectionLoader<>();
    }

    MetaEntity<?> elementEntity = relation.getMetaEntity();

    return new CollectionElementLoader<>(jdbcTemplate, metaEntity, elementEntity, relation);
  }

  @Override
  public Optional<T> load(Long id) {
    String targetColumn = getPrimaryKeyDbColumn();

    String query = selectQueryBuilder.createSelectByFieldQuery(metaEntity.getColumnClauseWithId(),
        metaEntity.getTableName(), targetColumn, id);
    String joinQuery = getJoinQuery(List.of(String.valueOf(id)));

    T entity = jdbcTemplate.queryForObject(query, rowMapper);

    MetaDataColumn relationColumn = metaEntity.getMetaDataColumns()
        .getColumnByFieldName(relation.getFieldName());
    if (relation.getFetchType() == FetchType.LAZY) {

      Enhancer enhancer = new Enhancer();
      enhancer.setSuperclass(relation.getCollectionType());
      enhancer.setCallback(new MethodLazyLoader(joinQuery, elementRowMapper));
      relationColumn.setFieldValue(entity, enhancer.create());

      return Optional.ofNullable(entity);
    }

    relationColumn.setFieldValue(entity, jdbcTemplate.queryForObject(joinQuery, elementRowMapper));

    return Optional.ofNullable(entity);

  }


  @Override
  public List<T> loadByIds(List<Long> ids) {
    String targetColumn = getPrimaryKeyDbColumn();

    List<String> idValues = ids.stream().map(Object::toString).collect(Collectors.toList());
    String query = selectQueryBuilder.createSelectByFieldsQuery(metaEntity.getColumnClauseWithId(),
        metaEntity.getTableName(), targetColumn, idValues);
    String joinQuery = getJoinQuery(idValues);

    List<T> entities = jdbcTemplate.query(query, rowMapper);
    MetaDataColumn relationColumn = metaEntity.getMetaDataColumns()
        .getColumnByFieldName(relation.getFieldName());

    if (relation.getFetchType() == FetchType.LAZY) {

      Enhancer enhancer = new Enhancer();
      Class<?> dsf = relation.getCollectionType().getDeclaringClass();
      enhancer.setSuperclass(relation.getCollectionType());
      enhancer.setCallback(new MethodLazyLoader(joinQuery, elementRowMapper));

      return entities.stream()
          .map(entity -> {
            relationColumn.setFieldValue(entity, enhancer.create());
            return entity;
          })
          .collect(Collectors.toList());
    }
    return entities.stream()
        .map(entity -> {
          relationColumn.setFieldValue(entity,
              jdbcTemplate.queryForObject(joinQuery, elementRowMapper));
          return entity;
        })
        .collect(Collectors.toList());

  }

  private String getPrimaryKeyDbColumn() {
    MetaDataColumn keyColumn = metaEntity.getPrimaryKeyColumn();

    return keyColumn.getDBColumnName();
  }

  private String getJoinQuery(List<String> id) {
    return new JoinQueryBuilder()
        .select(metaEntity.getEntityTableColumnsWithId(),
            elementEntity.getEntityTableColumnsWithId())
        .from(metaEntity.getTableName())
        .join(List.of(elementEntity.getTableName()))
        .on(List.of(relation.getDbName()))
        .where(metaEntity.getPrimaryKeyColumn().getDBColumnName(metaEntity.getTableName()), id)
        .build().createJoinQuery();
  }

  public class MethodLazyLoader implements LazyLoader {

    private final String joinQuery;
    private final CollectionRowMapper<?> elementRowMapper;

    public MethodLazyLoader(String joinQuery, CollectionRowMapper<?> rowMapper) {
      this.joinQuery = joinQuery;
      this.elementRowMapper = rowMapper;
    }

    @Override
    public Object loadObject() throws Exception {
      return jdbcTemplate.queryForObject(joinQuery, elementRowMapper);
    }

  }

}
