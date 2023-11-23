package persistence.action;

import java.util.LinkedHashSet;
import java.util.Set;

public class EntityActionSet<T extends AbstractEntityAction> {
    private final Set<T> actions;

    public EntityActionSet() {
        this.actions = new LinkedHashSet<>();
    }

    public boolean add(final T entityAction) {
        actions.remove(entityAction);
        return actions.add(entityAction);
    }

    public void executeAll() {
        actions.forEach(AbstractEntityAction::execute);
        actions.clear();
    }
}
