package hibernate.action;

public interface InsertUpdateActionQueue {

    void addAction(EntityBasicInsertAction<?> action);

    void addAction(EntityIdentityInsertAction<?> action);

    void addAction(EntityUpdateAction<?> action);
}
