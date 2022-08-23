package plan;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import utils.BackTracingIterator;
import table.Record;

public class FilterNode extends Node {
    List<FilterItem> items = new ArrayList<>();

    public void addItem(FilterItem item) {
        items.add(item);
    }

    public Set<String> getTableNames() {
        Set<String> tableNames = new HashSet<>();
        for (FilterItem item: items) {
            tableNames.add(item.tableName);
        }
        return tableNames;
    }

    public String toString() {
        return "Filter(" + items + ")";
    }

    @Override
    public Iterator<Record> iterator() {
        return backTracingIterator();
    }

    @Override
    public BackTracingIterator<Record> backTracingIterator() {
        return null;
    }

}

class Comparison {
    ComparisonType type;

    public Comparison(ComparisonType type) {
        this.type = type;
    }

    public static Comparison valueOf(String comp) {
        switch (comp) {
        case "=":
            return new Comparison(ComparisonType.EQUAL_TO);
        case ">":
            return new Comparison(ComparisonType.GREATER_THAN);
        case "<":
            return new Comparison(ComparisonType.LESS_THAN);
        case ">=":
            return new Comparison(ComparisonType.GREATER_THAN_OR_EQUAL_TO);
        case "<=":
            return new Comparison(ComparisonType.LESS_THAN_OR_EQUAL_TO);
        case "!=":
            return new Comparison(ComparisonType.NOT_EQUAL_TO);
        default:
            return null;
        }
    }

    public String toString() {
        return type.toString();
    }
}

enum ComparisonType {
    EQUAL_TO,
    NOT_EQUAL_TO,
    LESS_THAN,
    LESS_THAN_OR_EQUAL_TO,
    GREATER_THAN,
    GREATER_THAN_OR_EQUAL_TO

}

class FilterItem {
    String tableName;
    String columnName;
    Comparison comparison;
    String comLiteral;

    public void setComparison(Comparison comparison) {
        this.comparison = comparison;
    }

    public void setComLiteral(String comLiteral) {
        this.comLiteral = comLiteral;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String toString() {
        return "FilterItem(" + tableName + "." + columnName + " " + comparison + " " + comLiteral + ")";
    }
}


