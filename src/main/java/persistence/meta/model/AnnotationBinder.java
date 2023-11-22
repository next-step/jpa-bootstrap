package persistence.meta.model;

import jakarta.persistence.Entity;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import persistence.entity.persistentcontext.EntityPersister;
import persistence.entity.persistentcontext.JdbcEntityPersister;
import persistence.meta.MetaEntity;

public class AnnotationBinder {
  private final ComponentScanner componentScanner;
  public AnnotationBinder(ComponentScanner componentScanner) {
    this.componentScanner = componentScanner;
  }

  public MetaModel buildMetaModel(Connection connection, String basePackage)
      throws IOException, ClassNotFoundException {

    List<Class<?>> clazzes = componentScanner.scan(basePackage)
        .stream()
        .filter(clazz -> clazz.isAnnotationPresent(Entity.class))
        .collect(Collectors.toList());

    Map<Class<?>, EntityPersister<?>> persisterMapping = clazzes.stream()
        .collect(Collectors.toMap(clazz -> clazz, clazz -> new JdbcEntityPersister(clazz, connection, MetaEntity.of(clazz))));

    return new MetaModelImpl(persisterMapping);
  }
}
