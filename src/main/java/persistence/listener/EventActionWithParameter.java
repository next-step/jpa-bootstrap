package persistence.listener;

@FunctionalInterface
public interface EventActionWithParameter<T, U, X> {

    void applyEventToListener(T eventListener, U action, X param);

}
