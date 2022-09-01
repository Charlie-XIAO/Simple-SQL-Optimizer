package plan;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import utils.TableColumnTuple;

public class SortNode extends Node {

    List<SortItem> sortItems = new ArrayList<>();
    // RBO column pruning required slot
    Set<TableColumnTuple<String, String>> _required = new HashSet<>();

    public void addSortItem(SortItem sortItem) {
        sortItems.add(sortItem);
        TableColumnTuple<String, String> newRequired = new TableColumnTuple<String, String>(sortItem.table, sortItem.column);
        if (!_required.contains(newRequired)) {
            _required.add(newRequired);
        }
    }

    public Set<TableColumnTuple<String, String>> getRequired() {
        return _required;
    }

    public String toString() {
        return "Sort(" + sortItems.toString() + ")";
    }
}

class SortItem {
    AggregateFunc func;
    String table;
    String column;
    boolean isDesc;

    public SortItem(AggregateFuncType func, String table, String column, boolean isDesc) {
        this.func = new AggregateFunc(func);
        this.table = table;
        this.column = column;
        this.isDesc = isDesc;
    }

    public String toString() {
        return "SortItem(" + table + "." + column + " " + (isDesc ? "DESC" : "ASC") + ")";
    }
}
