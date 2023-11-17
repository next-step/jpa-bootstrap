package hibernate.entity;

import hibernate.action.ActionQueue;
import hibernate.metamodel.MetaModel;

public interface EntitySource {

    MetaModel getMetaModel();

    ActionQueue getActionQueue();
}
