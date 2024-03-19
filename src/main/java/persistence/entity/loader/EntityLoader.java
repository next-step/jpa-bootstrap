package persistence.entity.loader;

import java.util.List;
import java.util.Map;
import persistence.sql.meta.Column;

public interface EntityLoader<T> {

    T find(Long id);

    List<T> find(Map<Column, Object> conditions);
}
