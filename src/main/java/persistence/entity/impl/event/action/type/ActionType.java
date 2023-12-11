package persistence.entity.impl.event.action.type;

public enum ActionType {
    INSERT(1),
    UPDATE(2),
    DELETE(3);

    ActionType(int priority) {
        this.priority = priority;
    }

    private int priority;

    public int getPriority() {
        return priority;
    }
}
