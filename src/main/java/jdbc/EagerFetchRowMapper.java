package jdbc;

import persistence.meta.Metamodel;
import persistence.sql.definition.TableAssociationDefinition;
import persistence.sql.definition.TableDefinition;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public class EagerFetchRowMapper<T> extends AbstractRowMapper<T> {
    private final Class<?> clazz;
    private final Metamodel metamodel;

    public EagerFetchRowMapper(Class<T> clazz, Metamodel metamodel) {
        super(clazz, metamodel);
        this.clazz = clazz;
        this.metamodel = metamodel;
    }

    @Override
    protected void setAssociation(ResultSet resultSet, T instance) throws NoSuchFieldException, SQLException {
        do {
            List<TableAssociationDefinition> associations = metamodel.getTableDefinition(clazz).getAssociations();
            if (associations.isEmpty()) {
                return;
            }

            for (TableAssociationDefinition association : associations) {
                if (!association.isEager()) {
                    continue;
                }

                final Object associatedInstance = newInstance(association.getAssociatedEntityClass());
                setColumns(resultSet, metamodel.getTableDefinition(association.getAssociatedEntityClass()),
                        associatedInstance);

                final Collection<Object> entityCollection = association.getCollectionField(instance);
                entityCollection.add(associatedInstance);
            }
        } while (resultSet.next());
    }
}
