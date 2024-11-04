package jdbc;

import persistence.meta.Metamodel;
import persistence.sql.definition.TableAssociationDefinition;
import persistence.sql.definition.TableDefinition;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public class EagerFetchRowMapper<T> extends AbstractRowMapper<T> {
    private final TableDefinition parentTableDefinition;
    private final TableDefinition associatedTableDefinition;

    public EagerFetchRowMapper(Class<T> clazz, TableDefinition parentTableDefinition,
                               TableDefinition associatedTableDefinition, Metamodel metamodel) {
        super(clazz, metamodel);
        this.parentTableDefinition = parentTableDefinition;
        this.associatedTableDefinition = associatedTableDefinition;
    }

    @Override
    protected void setAssociation(ResultSet resultSet, T instance) throws NoSuchFieldException, SQLException {
        do {
            List<TableAssociationDefinition> associations = parentTableDefinition.getAssociations();
            if (associations.isEmpty()) {
                return;
            }

            for (TableAssociationDefinition association : associations) {
                if (!association.isEager()) {
                    continue;
                }

                final Object associatedInstance = newInstance(association.getAssociatedEntityClass());
                setColumns(resultSet, associatedTableDefinition, associatedInstance);

                final Collection<Object> entityCollection = association.getCollectionField(instance);
                entityCollection.add(associatedInstance);
            }
        } while (resultSet.next());
    }
}
