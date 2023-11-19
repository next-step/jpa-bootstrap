package persistence.listener;

@FunctionalInterface
public interface LoadEventListener {
    LoadType RELOAD = new LoadType("RELOAD");
    LoadType LOAD = new LoadType("LOAD");

    void onLoad(LoadEvent eventType, LoadType loadType);

    final class LoadType {
        private final String name;

        private LoadType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
