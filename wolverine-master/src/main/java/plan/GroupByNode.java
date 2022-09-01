package plan;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import utils.TableColumnTuple;

public class GroupByNode extends Node {

    List<GroupByItem> items = new ArrayList<>();
    // RBO column pruning required slot
    Set<TableColumnTuple<String, String>> _required = new HashSet<>();

    public void addItem(GroupByItem item) {
        items.add(item);
        TableColumnTuple<String, String> newRequired = new TableColumnTuple<String,String>(item.tableName, item.columnName);
        if (!_required.contains(newRequired)) {
            _required.add(newRequired);
        }
    }

    public Set<TableColumnTuple<String, String>> getRequired() {
        return _required;
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
        return "GroupByItem(" + tableName + "." + columnName + ")";
    }
}
