package persistence.sql.schema.constraint;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.lang.reflect.Field;
import java.util.Objects;
import persistence.sql.dialect.Dialect;
import persistence.sql.exception.EntityMappingException;

public class PrimaryKeyConstraint implements Constraint {

    private static final String PRIMARY_KEY_FORMAT = "%s %s";
    private static final String PRIMARY_KEY = "PRIMARY KEY";

    private final String constraint;

    public PrimaryKeyConstraint(Field field, Dialect dialect) {
        this.constraint = extractGeneratedValueStrategy(field, dialect);
    }

    @Override
    public String getConstraint() {
        return constraint;
    }

    private String extractGeneratedValueStrategy(Field field, Dialect dialect) {
        if (!isPrimaryKey(field)) {
            return "";
        }

        if (!field.isAnnotationPresent(GeneratedValue.class)) {
            return PRIMARY_KEY;
        }

        final GeneratedValue generatedValue = field.getAnnotation(GeneratedValue.class);

        if (Objects.requireNonNull(generatedValue.strategy()) == GenerationType.IDENTITY) {
            return String.format(PRIMARY_KEY_FORMAT, dialect.generationIdentity(), PRIMARY_KEY);
        }

        throw EntityMappingException.unrecognizedGeneratedValue(generatedValue.strategy().name());
    }

    public static boolean isPrimaryKey(Field field) {
        return field.isAnnotationPresent(Id.class);
    }
}
