package persistence.sql.entity;

import jakarta.persistence.FetchType;
import jakarta.persistence.Table;
import persistence.sql.dml.exception.NotFoundIdException;
import persistence.sql.entity.model.DomainType;
import persistence.sql.entity.model.DomainTypes;
import persistence.sql.entity.model.PrimaryDomainType;
import persistence.sql.entity.model.TableName;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EntityMappingTable {

    private final TableName tableName;
    private final DomainTypes domainTypes;
    private final Table table;

    private EntityMappingTable(final TableName tableName,
                               final DomainTypes domainTypes,
                               final Table table) {
        this.tableName = tableName;
        this.domainTypes = domainTypes;
        this.table = table;
    }

    public static EntityMappingTable from(final Class<?> clazz) {
        return new EntityMappingTable(
                new TableName(clazz.getSimpleName()),
                DomainTypes.from(clazz.getDeclaredFields()),
                getTable(clazz)
        );
    }

    public static EntityMappingTable of(final Class<?> clazz,
                                        final Object object) {
        return new EntityMappingTable(
                new TableName(clazz.getSimpleName()),
                DomainTypes.of(clazz.getDeclaredFields(), object),
                getTable(clazz)
        );
    }

    private static Table getTable(final Class<?> clazz) {
        return clazz.isAnnotationPresent(Table.class) ?
                clazz.getAnnotation(Table.class) :
                null;
    }

    public TableName getTable() {
        if (table != null) {
            return new TableName(table.name());
        }

        return tableName;
    }

    public String getTableName() {
        return getTable().getName();
    }

    public Stream<DomainType> getDomainTypeStream() {
        return domainTypes.getDomainTypeStream();
    }

    public List<String> getColumnName() {
        return domainTypes.getColumnName();
    }

    public String getAliasAndTableName() {
        return getTable().getAliasAndTableName();
    }

    public String getAlias() {
        return getTable().getAlias();
    }

    public DomainTypes getDomainTypes() {
        return domainTypes;
    }

    public PrimaryDomainType getPkDomainTypes() {
        return (PrimaryDomainType) domainTypes.getDomainTypeStream()
                .filter(DomainType::isPrimaryDomain)
                .findFirst()
                .orElseThrow(NotFoundIdException::new);
    }

    public boolean hasFetchType(final FetchType fetchType) {
        return domainTypes.getDomainTypes()
                .stream()
                .anyMatch(domainType -> domainType.getFetchType() == fetchType);
    }

    public List<DomainType> getDomainTypeWithLazyLoading() {
        return domainTypes.getDomainTypes()
                .stream()
                .filter(domainType -> domainType.getFetchType() == FetchType.LAZY)
                .collect(Collectors.toList());
    }

}