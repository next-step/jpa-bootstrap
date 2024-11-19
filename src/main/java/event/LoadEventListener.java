package event;

public interface LoadEventListener {
    void onLoad();
    <T> T onLoad(T t);

}
