package persistence.action;

public interface ActionQueueFront {
    void executeInsert();
    void executeDelete();
    void executeUpdate();
    void flush();

}
