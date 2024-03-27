package database.mapping;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EntityAssociationMetadata {
    private final Class<?> clazz;
    private final List<Field> associationFields;

    public static List<Association> associationsOf(Class<?> clazz) {
        return new EntityAssociationMetadata(clazz).getAssociations();
    }

    public EntityAssociationMetadata(Class<?> clazz) {
        this.clazz = clazz;

        associationFields = Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(OneToMany.class))
                .collect(Collectors.toList());
    }

    public List<Association> getAssociationsRelatedTo(List<Class<?>> entityClasses) {
        return entityClasses.stream()
                .filter(this::exceptMe)
                .flatMap(this::getAssociationsBetweenMeAndOther)
                .collect(Collectors.toList());
    }

    private boolean exceptMe(Class<?> entity) {
        return entity != clazz;
    }

    private Stream<Association> getAssociationsBetweenMeAndOther(Class<?> other) {
        return associationsOf(other)
                .stream()
                .filter(this::isConnectedToMe);
    }

    private boolean isConnectedToMe(Association association) {
        return association.getFieldGenericType() == clazz;
    }

    public List<Association> getAssociations() {
        return associationFields.stream()
                .filter(EntityAssociationMetadata::checkAssociationAnnotation)
                .map(Association::fromField)
                .collect(Collectors.toList());
    }

    public boolean hasAssociations() {
        return associationFields.stream().anyMatch(EntityAssociationMetadata::checkAssociationAnnotation);
    }

    private static boolean checkAssociationAnnotation(Field field) {
        return field.isAnnotationPresent(OneToMany.class) && field.isAnnotationPresent(JoinColumn.class);
    }
}
