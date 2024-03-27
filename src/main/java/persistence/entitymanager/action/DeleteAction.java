package persistence.entitymanager.action;

public class DeleteAction implements Action {
    private final Object entity;

    public DeleteAction(Object entity) {
        this.entity = entity;
    }

    public Object getEntity() {
        return entity;
    }
}
