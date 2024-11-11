package persistence.meta;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Objects;

public class ColumnValue {
    private static final Logger logger = LoggerFactory.getLogger(ColumnValue.class);

    private final Class<?> type;
    private final Object value;

    public ColumnValue(Field field) {
        this.type = field.getType();
        this.value = null;
    }

    public ColumnValue(Field field, Object entity) {
        this.type = field.getType();
        this.value = getValue(field, entity);
    }

    public Object value() {
        return value;
    }

    private Object getValue(Field field, Object entity) {
        try {
            field.setAccessible(true);
            return field.get(entity);
        } catch (IllegalAccessException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ColumnValue that = (ColumnValue) o;
        return Objects.equals(type, that.type) && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, value);
    }
}
