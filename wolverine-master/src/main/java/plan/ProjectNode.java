package plan;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import utils.TableColumnTuple;

import table.Record;

public class ProjectNode extends Node {

    private List<ProjectItem> items = new ArrayList<>();
    // RBO column pruning required slot
    private Set<TableColumnTuple<String, String>> _required = new HashSet<>();

    public void addItem(ProjectItem item) {
        items.add(item);
        TableColumnTuple<String, String> newRequired = new TableColumnTuple<String, String>(item.tableName, item.columnName);
        if (!_required.contains(newRequired)) {
            _required.add(newRequired);
        }
    }

    public Set<TableColumnTuple<String, String>> getRequired() {
        return _required;
    }

    public String toString() {
        return "Project(" + items.toString() + ")";
    }

    @Override
    public Iterator<Record> iterator() {
        return this.getChild().iterator();
    }
}

class ProjectItem {
    AggregateFunc func;
    String tableName;
    String columnName;

    public ProjectItem(AggregateFuncType func, String tableName, String columnName) {
        this.func = new AggregateFunc(func);
        this.tableName = tableName;
        this.columnName = columnName;
    }

    public String toString() {
        return "ProjectItem(" + func + " " + tableName + "." + columnName + ")";
    }
}