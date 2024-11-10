package jdbc;

import persistence.entity.EntityPersister;
import persistence.sql.definition.TableAssociationDefinition;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public class EagerFetchRowMapper<T> extends AbstractRowMapper<T> {
    private final EntityPersister parentPersister;
    private final EntityPersister associatedPersister;

    public EagerFetchRowMapper(Class<T> clazz,
                               EntityPersister parentPersister,
                               EntityPersister associatedPersister) {

        super(clazz, parentPersister);
        this.parentPersister = parentPersister;
        this.associatedPersister = associatedPersister;
    }

    @Override
    protected void setAssociation(ResultSet resultSet, T instance) throws NoSuchFieldException, SQLException {
        do {
            List<TableAssociationDefinition> associations = parentPersister.getAssociations();
            if (associations.isEmpty()) {
                return;
            }

            for (TableAssociationDefinition association : associations) {
                if (!association.isEager()) {
                    continue;
                }

                final Object associatedInstance = newInstance(association.getAssociatedEntityClass());
                setColumns(resultSet, associatedPersister, associatedInstance);

                final Collection<Object> entityCollection = association.getCollectionField(instance);
                entityCollection.add(associatedInstance);
            }
        } while (resultSet.next());
    }
}
