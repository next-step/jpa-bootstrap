package persistence.exception;

public class CurrentSessionAlreadyOpenException extends PersistenceException {
    public CurrentSessionAlreadyOpenException() {
        super("이미 열린 Session 정보가 존재합니다.");
    }

}
