package plan;

import table.Record;
import table.Statistics;
import table.Table;
import table.Column;
import plan.type.JoinType;
import plan.type.PhysicalJoinType;
import utils.TableColumnTuple;
import utils.BackTracingIterator;
import utils.DummyNestedLoopJoinIterator;
import utils.BlockNestedLoopJoinIterator;
import utils.HashJoinIterator;
import utils.SortMergeJoinIterator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class JoinNode extends Node {

    private JoinType joinType;
    private String tableNameLeft;
    private String tableNameRight;
    private String columnNameLeft;
    private String columnNameRight;
    private boolean isLeftOutside = true;
    private PhysicalJoinType physicalJoinType = PhysicalJoinType.DUMMY_NESTED_LOOP_JOIN;
    // JoinNode has `left` and `right` instead of `child` in its parent class Node
    private Node left;
    private Node right;
    // for data storage
    public Table table = new Table();
    public List<Record> records = new ArrayList<>();
    // RBO column pruning required slot
    private Set<TableColumnTuple<String, String>> _required = new HashSet<>();
    // CBO required statistics
    private Statistics statistics = new Statistics();
    // CBO join reorder required information
    private Set<String> _contained;

    public void setLeft(Node left) {
        this.left = left;
        left.setParent(this);
    }

    public void setRight(Node right) {
        this.right = right;
        right.setParent(this);
    }

    public Node getLeft() {
        return this.left;
    }

    public Node getRight() {
        return this.right;
    }

    /**
     * make sure to traverse the left-deep tree correctly
     * with only the `getChild()` method
     */

    @Override
    public Node getChild() {
        return this.left;
    }

    @Override
    public boolean isLeaf() {
        return this.left == null;
    }

    @Override
    public boolean isJoinNode() {
        return true;
    }

    public void setJoinType(JoinType joinType) {
        this.joinType = joinType;
    }

    @Deprecated
    public void setTableNameLeft(String tableNameLeft) {
        this.tableNameLeft = tableNameLeft;
        //_required_tables.add(tableNameLeft);
    }

    @Deprecated
    public void setTableNameRight(String tableNameRight) {
        this.tableNameRight = tableNameRight;
        //_required_tables.add(tableNameRight);
    }

    @Deprecated
    public void setColumnNameLeft(String columnNameLeft) {
        this.columnNameLeft = columnNameLeft;
        //_required_columns.add(columnNameLeft);
    }

    @Deprecated
    public void setColumnNameRight(String columnNameRight) {
        this.columnNameRight = columnNameRight;
        //_required_columns.add(columnNameRight);
    }

    public void setTableColumnLeft(String tableNameLeft, String columnNameLeft) {
        this.tableNameLeft = tableNameLeft;
        this.columnNameLeft = columnNameLeft;
        TableColumnTuple<String, String> newRequired = new TableColumnTuple<String, String>(tableNameLeft, columnNameLeft);
        if (!_required.contains(newRequired)) {
            _required.add(newRequired);
        }
    }

    public void setTableColumnRight(String tableNameRight, String columnNameRight) {
        this.tableNameRight = tableNameRight;
        this.columnNameRight = columnNameRight;
        TableColumnTuple<String, String> newRequired = new TableColumnTuple<String, String>(tableNameRight, columnNameRight);
        if (!_required.contains(newRequired)) {
            _required.add(newRequired);
        }
    }

    public void setContained(Set<String> _contained) {
        this._contained = _contained;
    }

    public void setStatistics(Statistics statistics) {
        this.statistics = statistics;
    }

    public String getTableNameLeft() {
        return tableNameLeft;
    }

    public String getTableNameRight() {
        return tableNameRight;
    }

    public String getColumnNameLeft() {
        return columnNameLeft;
    }
    
    public String getColumnNameRight() {
        return columnNameRight;
    }

    public JoinType getJoinType() {
        return joinType;
    }

    public Set<TableColumnTuple<String, String>> getRequired() {
        return _required;
    }

    public Statistics getStatistics() {
        return statistics;
    }

    public Set<String> getContained() {
        if (_contained != null) {
            return _contained;
        }
        Set<String> _contained = new HashSet<>();
        if (left instanceof ScanNode) {
            _contained.add(((ScanNode) left).getTableName());
        }
        else if (left instanceof FilterNode) {
            _contained.addAll(((FilterNode) left).getTableNames());
        }
        else if (left instanceof JoinNode) {
            _contained.addAll(((JoinNode) left).getContained());
        }
        if (right instanceof ScanNode) {
            _contained.add(((ScanNode) right).getTableName());
        }
        else if (right instanceof FilterNode) {
            _contained.addAll(((FilterNode) right).getTableNames());
        }
        else if (right instanceof JoinNode) {
            _contained.addAll(((JoinNode) right).getContained());
        }
        this._contained = _contained;
        return _contained;
    }

    public String toString() {
        return "Join(" + joinType + " " + tableNameLeft + "." + columnNameLeft + " " + tableNameRight + "."
            + columnNameRight + ")";
    }

    @Deprecated
    public void validate() {
        List<String> leftTableNames;
        if (left instanceof ScanNode) {
            leftTableNames = ((ScanNode) left).table.getTableNames();
        }
        else if (left instanceof FilterNode) {
            leftTableNames = ((FilterNode) left).table.getTableNames();
        }
        else if (left instanceof JoinNode) {
            leftTableNames = ((JoinNode) left).table.getTableNames();
        }
        else {
            throw new Error("JoinNode might have invalid child nodes.");
        }
        if (!leftTableNames.contains(tableNameLeft)) {
            String temp;
            temp = tableNameLeft;
            tableNameLeft = tableNameRight;
            tableNameRight = temp;
            temp = columnNameLeft;
            columnNameLeft = columnNameRight;
            columnNameRight = temp;
            if (!leftTableNames.contains(tableNameLeft)) {
                throw new Error("Join predicate might be incorrect.");
            }
        }
    }

    public void setPhysicalJoinType(PhysicalJoinType physicalJoinType) {
        this.physicalJoinType = physicalJoinType;
    }

    public void setTableSchema(List<Column> schema) {
        for (Column column: schema) {
            String tableName = column.getTableName();
            if (!table.getTableNames().contains(tableName)) {
                table.addTableName(tableName);
            }
            table.addField(tableName, column.getColName(), column.getColType(), column.getColSize());
        }
    }

    public void setRecords(List<Record> records) {
        this.records = records;
    }

    @Override
    public Iterator<Record> iterator() {
        return backTracingIterator();
    }

    @Override
    public BackTracingIterator<Record> backTracingIterator() {
        switch (physicalJoinType) {
        case DUMMY_NESTED_LOOP_JOIN:
            return new DummyNestedLoopJoinIterator(this, isLeftOutside);
        case BLOCK_NESTED_LOOP_JOIN:
            return new BlockNestedLoopJoinIterator(this, isLeftOutside);
        case HASH_JOIN:
            return new HashJoinIterator(this, isLeftOutside);
        case SORT_MERGE_JOIN:
            return new SortMergeJoinIterator(this, isLeftOutside);
        default:
            return null;
        }
    }

}