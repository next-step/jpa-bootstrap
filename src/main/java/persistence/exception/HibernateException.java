package persistence.exception;

public class HibernateException extends RuntimeException {
    public HibernateException(String e) {
        super(e);
    }
}
