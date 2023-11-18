package persistence.entity.manager;

@FunctionalInterface
public interface SessionCloseStrategy {

    void close();

}
