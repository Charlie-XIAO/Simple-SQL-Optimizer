package plan;

import java.util.ArrayList;
import java.util.List;

public class GroupByNode extends Node {
    List<GroupByItem> items = new ArrayList<>();

    public void addItem(GroupByItem item) {
        items.add(item);
    }

    public String toString() {
        return "GroupBy(" + items + ")";
    }
}

class GroupByItem {
    public String tableName;
    public String columnName;

    public GroupByItem(String tableName, String columnName) {
        this.tableName = tableName;
        this.columnName = columnName;
    }

    public String toString() {
        return "GroupByItem(" + tableName + "," + columnName + ")";
    }
}
