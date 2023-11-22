package hibernate.action;

public interface DeleteActionQueue {

    void addAction(EntityDeleteAction<?> action);
}
