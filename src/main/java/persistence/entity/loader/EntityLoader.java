package persistence.entity.loader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.model.PersistentClass;

import java.util.List;

public interface EntityLoader {

    Logger log = LoggerFactory.getLogger(EntityLoader.class);

    <T> List<T> load(final PersistentClass<T> persistentClass, final Object key);

}
