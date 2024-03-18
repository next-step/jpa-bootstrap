package persistence.sql.meta;

public class TableFactory {

    ComponentScanner scanner = new ComponentScanner();

    private static class Holder {
        static final TableFactory INSTANCE = new TableFactory();
    }

    private TableFactory() {
        ComponentScanner scanner = new ComponentScanner();
    }

    public static TableFactory getInstance() {
        return Holder.INSTANCE;
    }
}
