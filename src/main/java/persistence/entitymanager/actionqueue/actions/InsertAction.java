package persistence.entitymanager.actionqueue.actions;

public class InsertAction implements Action {
    private final Object entity;

    public InsertAction(Object entity) {
        this.entity = entity;
    }

    public Object getEntity() {
        return entity;
    }
}
