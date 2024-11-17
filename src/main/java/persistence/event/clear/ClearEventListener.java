package persistence.event.clear;

import persistence.event.EventListener;

public interface ClearEventListener extends EventListener {
    void onClear(ClearEvent clearEvent);
}
