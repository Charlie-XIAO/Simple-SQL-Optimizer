package plan;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import table.Record;

public class ProjectNode extends Node {
    private List<ProjectItem> items = new ArrayList<>();

    public void addItem(ProjectItem item) {
        items.add(item);
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