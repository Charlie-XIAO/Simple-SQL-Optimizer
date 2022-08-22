package plan;

import java.util.ArrayList;
import java.util.List;

public class SortNode extends Node {

    List<SortItem> sortItems = new ArrayList<>();

    public void addSortItem(SortItem sortItem) {
        sortItems.add(sortItem);
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
