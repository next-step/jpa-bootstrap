package persistence.entity.event.action;

public enum ActionType {
    INSERT,
    UPDATE,
    DELETE,
    NONE,
    ;

    public boolean isNoneAction() {
        return this == NONE;
    }
}
