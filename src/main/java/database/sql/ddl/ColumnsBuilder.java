package database.sql.ddl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ColumnsBuilder {
    private final Map<String, Boolean> columnsMap = new HashMap<>();
    private final List<String> columns = new ArrayList<>();

    public void add(String columnName, String columnDefinition) {
        if (columnsMap.containsKey(columnName)) {
            return;
        }
        columnsMap.put(columnName, true);
        columns.add(columnDefinition);
    }

    public List<String> toList() {
        return new ArrayList<>(columns);
    }
}
