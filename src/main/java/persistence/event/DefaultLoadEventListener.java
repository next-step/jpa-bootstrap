package persistence.event;

import persistence.entity.EntityLoader;

public class DefaultLoadEventListener implements LoadEventListener {

//    private final EntityLoader entityLoader;

    @Override
    public <T> T onLoad(LoadEvent event) {
        return null;
    }
}
