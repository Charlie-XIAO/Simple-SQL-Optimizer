package plan;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
//import javax.script.ScriptEngine;
//import javax.script.ScriptEngineManager;

import utils.TableColumnTuple;
import utils.BackTracingIterator;
import utils.ListBacktracingIterator;
import table.Record;
import table.Statistics;
import table.Table;

public class FilterNode extends Node {

    List<FilterItem> items = new ArrayList<>();
    // for data storage
    public Table table = new Table();
    public List<Record> records = new ArrayList<>();
    // RBO column pruning required slot
    Set<TableColumnTuple<String, String>> _required = new HashSet<>();
    // CBO required statistics
    Statistics statistics = new Statistics();

    public void addItem(FilterItem item) {
        items.add(item);
        TableColumnTuple<String, String> newRequired = new TableColumnTuple<String, String>(item.tableName, item.columnName);
        if (!_required.contains(newRequired)) {
            _required.add(newRequired);
        }
    }

    public Set<String> getTableNames() {
        Set<String> tableNames = new HashSet<>();
        for (FilterItem item: items) {
            tableNames.add(item.tableName);
        }
        return tableNames;
    }

    public FilterNode selectByTableName(String tableName) {
        FilterNode filterNode = new FilterNode();
        for (FilterItem filterItem: items) {
            if (filterItem.tableName.equals(tableName)) {
                filterNode.addItem(filterItem);
            }
        }
        return filterNode;
    }

    public int getItemCount() {
        return items.size();
    }

    public Set<TableColumnTuple<String, String>> getRequired() {
        return _required;
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
        //filterRecords();
        return new ListBacktracingIterator(records);
    }

    /*
    private void filterRecords() {
        Node child = this.getChild();
        List<Record> targetRecords = new ArrayList<>();
        if (child instanceof ScanNode) {
            ScanNode target = (ScanNode) child;
            targetRecords = target.records;
            this.table = target.table;
        }
        else if (child instanceof JoinNode) {
            JoinNode target = (JoinNode) child;
            targetRecords = target.records;
            this.table = target.table;
        }
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
        for (FilterItem item: items) {
            int index = table.getColumnNames().indexOf(item.columnName);
            String compInfo = item.comparison.getComp() + "'" + item.comLiteral + "'";
            String expression;
            //ColumnType columnType = table.getSchema().get(index).getColType();
            for (Record record: targetRecords) {
                expression = "'" + record.getData().get(index).getEvalExpression() + "'" + compInfo;
                try {
                    if ((boolean) engine.eval(expression)) {
                        this.records.add(record);
                    }
                }
                catch (Exception e) {
                    System.out.println("Error filtering records: " + e);
                    this.records.add(record);
                }
            }
        }
    }
    */

}

class Comparison {

    ComparisonType type;
    String comp;

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

    public String getComp() {
        switch (type) {
            case EQUAL_TO:
                return "==";
            case GREATER_THAN:
                return ">";
            case GREATER_THAN_OR_EQUAL_TO:
                return ">=";
            case LESS_THAN:
                return "<";
            case LESS_THAN_OR_EQUAL_TO:
                return "<=";
            case NOT_EQUAL_TO:
                return "!=";
            default:
                return "?";
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
    GREATER_THAN_OR_EQUAL_TO;

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