package persistence.meta.model;

import jakarta.persistence.Entity;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import jdbc.JdbcTemplate;
import persistence.entity.persistentcontext.EntityPersister;
import persistence.entity.persistentcontext.JdbcEntityPersister;
import persistence.meta.MetaEntity;

public class AnnotationBinder {
  private final ComponentScanner componentScanner;
  public AnnotationBinder(ComponentScanner componentScanner) {
    this.componentScanner = componentScanner;
  }

  public MetaModel buildMetaModel(JdbcTemplate jdbcTemplate, String basePackage)
      throws IOException, ClassNotFoundException {

    List<Class<?>> clazzes = componentScanner.scan(basePackage)
        .stream()
        .filter(clazz -> clazz.isAnnotationPresent(Entity.class))
        .collect(Collectors.toList());
    Map<Class<?>, MetaEntity<?>> metaEntityMapping = clazzes.stream()
        .collect(Collectors.toMap(clazz -> clazz, MetaEntity::of));
    Map<Class<?>, EntityPersister<?>> persisterMapping = clazzes.stream()
        .collect(Collectors.toMap(clazz -> clazz, clazz -> new JdbcEntityPersister(jdbcTemplate, metaEntityMapping.get(clazz))));

    return new MetaModelImpl(persisterMapping);
  }
}
