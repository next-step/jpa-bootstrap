package persistence.entitymanager.actionqueue.actions;

public class UpdateAction implements Action {
    private final Object entity;

    public UpdateAction(Object entity) {
        this.entity = entity;
    }

    public Object getEntity() {
        return entity;
    }
}
