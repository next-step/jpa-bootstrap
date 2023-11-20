package persistence.core;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import persistence.exception.PersistenceException;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class EntityMetadata<T> {

    private final Class<T> clazz;
    private final String tableName;
    private final EntityColumns columns;
    private final EntityIdColumn idColumn;
    private final List<EntityOneToManyColumn> oneToManyColumns;
    private final List<EntityManyToOneColumn> manyToOneColumns;

    private EntityMetadata(final Class<T> clazz) {
        this.validate(clazz);
        this.clazz = clazz;
        this.tableName = initTableName(clazz);
        this.columns = new EntityColumns(clazz, tableName);
        this.idColumn = this.columns.getId();
        this.oneToManyColumns = this.columns.getOneToManyColumns();
        this.manyToOneColumns = this.columns.getManyToOneColumns();
    }

    public static <T> EntityMetadata<T> from(final Class<T> clazz) {
        return new EntityMetadata<>(clazz);
    }

    private void validate(final Class<T> clazz) {
        if (!clazz.isAnnotationPresent(Entity.class)) {
            throw new PersistenceException(clazz.getName() + "은 Entity 클래스가 아닙니다.");
        }
    }

    private String initTableName(final Class<?> clazz) {
        final Table tableAnnotation = clazz.getDeclaredAnnotation(Table.class);
        return Optional.ofNullable(tableAnnotation)
                .filter(table -> !table.name().isEmpty())
                .map(Table::name)
                .orElseGet(clazz::getSimpleName);
    }

    public String getTableName() {
        return this.tableName;
    }

    public EntityColumns getColumns() {
        return this.columns;
    }

    public List<String> getColumnNamesWithAlias() {
        return this.columns.stream()
                .map(this::getColumnNames)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    private List<String> getColumnNames(final EntityColumn entityColumn) {
        if (entityColumn.isOneToMany()) {
            final EntityOneToManyColumn oneToManyColumn = (EntityOneToManyColumn) entityColumn;
            if (oneToManyColumn.isFetchTypeLazy()) {
                return Collections.emptyList();
            }
            return oneToManyColumn.getAssociatedEntityColumnNamesWithAlias();
        }

        if (entityColumn.isManyToOne()) {
            final EntityManyToOneColumn manyToOneColumn = (EntityManyToOneColumn) entityColumn;
            if (manyToOneColumn.isFetchTypeLazy()) {
                return List.of(manyToOneColumn.getNameWithAlias());
            }
            return manyToOneColumn.getAssociatedEntityColumnNamesWithAlias();
        }

        return List.of(entityColumn.getNameWithAlias());
    }

    public List<String> toInsertableColumnNames() {
        return this.columns.stream()
                .filter(EntityColumn::isInsertable)
                .map(EntityColumn::getName)
                .collect(Collectors.toUnmodifiableList());
    }

    public List<String> toInsertableColumnFieldNames() {
        return this.columns.stream()
                .filter(EntityColumn::isInsertable)
                .map(EntityColumn::getFieldName)
                .collect(Collectors.toUnmodifiableList());
    }

    public String getIdColumnName() {
        return this.idColumn.getName();
    }

    public String getIdColumnNameWithAlias() {
        return this.idColumn.getNameWithAlias();
    }

    public String getIdColumnFieldName() {
        return this.idColumn.getFieldName();
    }

    public List<String> toColumnNames() {
        return this.columns.stream()
                .map(EntityColumn::getName)
                .collect(Collectors.toUnmodifiableList());
    }

    public List<String> toColumnFieldNames() {
        return this.columns.stream()
                .map(EntityColumn::getFieldName)
                .collect(Collectors.toUnmodifiableList());
    }

    public EntityIdColumn getIdColumn() {
        return this.idColumn;
    }

    public EntityColumns toInsertableColumn() {
        return new EntityColumns(
                this.columns.stream()
                        .filter(EntityColumn::isInsertable)
                        .collect(Collectors.toUnmodifiableList()));
    }

    public List<EntityOneToManyColumn> getOneToManyColumns() {
        return this.oneToManyColumns;
    }

    public List<EntityOneToManyColumn> getLazyOneToManyColumns() {
        return this.oneToManyColumns.stream()
                .filter(EntityAssociatedColumn::isFetchTypeLazy)
                .collect(Collectors.toUnmodifiableList());
    }

    public List<EntityOneToManyColumn> getEagerOneToManyColumns() {
        return this.oneToManyColumns.stream()
                .filter(EntityAssociatedColumn::isFetchTypeEager)
                .collect(Collectors.toUnmodifiableList());
    }

    public List<EntityManyToOneColumn> getLazyManyToOneColumns() {
        return this.manyToOneColumns.stream()
                .filter(EntityAssociatedColumn::isFetchTypeLazy)
                .collect(Collectors.toUnmodifiableList());
    }

    public List<EntityManyToOneColumn> getEagerManyToOneColumns() {
        return this.manyToOneColumns.stream()
                .filter(EntityAssociatedColumn::isFetchTypeEager)
                .collect(Collectors.toUnmodifiableList());
    }


    public boolean isType(final Class<?> clazz) {
        return this.clazz.equals(clazz);
    }

    public Class<T> getType() {
        return this.clazz;
    }

    public boolean hasOneToManyAssociatedOf(final EntityMetadata<?> entityMetadata) {
        return oneToManyColumns.stream()
                .anyMatch(entityOneToManyColumn -> entityMetadata.isType(entityOneToManyColumn.getJoinColumnType()));
    }

    public Class<?> getIdType() {
        return this.idColumn.getType();
    }

    public String getIdName() {
        return this.idColumn.getName();
    }

    public List<String> getColumnFieldNames() {
        return columns.getFieldNames();
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        final EntityMetadata<?> that = (EntityMetadata<?>) object;
        return Objects.equals(clazz, that.clazz) && Objects.equals(tableName, that.tableName) && Objects.equals(columns, that.columns) && Objects.equals(idColumn, that.idColumn) && Objects.equals(oneToManyColumns, that.oneToManyColumns) && Objects.equals(manyToOneColumns, that.manyToOneColumns);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clazz, tableName, columns, idColumn, oneToManyColumns, manyToOneColumns);
    }


}
