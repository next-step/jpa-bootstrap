package persistence.sql.dialect;

import java.lang.reflect.Field;

public interface Dialect {

    String generationIdentity();

    String getFieldType(Field field);
}
