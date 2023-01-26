package RBO;

import java.util.HashSet;
import java.util.Set;

import plan.ProjectNode;
import plan.ScanNode;
import plan.SortNode;
import plan.FilterNode;
import plan.GroupByNode;
import plan.HavingNode;
import plan.JoinNode;
import plan.Node;
import utils.TableColumnTuple;

public class ColumnPruning {
 
    private Node root;

    public ColumnPruning(Node root) {
        this.root = root;
    }

    protected void optimize() {
        Set<TableColumnTuple<String, String>> allRequired = new HashSet<>();
        Node curNode = root;
        while (!curNode.isLeaf()) {
            //System.out.println(curNode);
            //System.out.println("Tup: " + allRequired);
            //System.out.println();
            if (curNode instanceof FilterNode) {
                FilterNode tempNode = (FilterNode) curNode;
                for (TableColumnTuple<String, String> required: tempNode.getRequired()) {
                    if (!allRequired.contains(required)) {
                        allRequired.add(required);
                    }
                }
            }
            else if (curNode instanceof GroupByNode) {
                GroupByNode tempNode = (GroupByNode) curNode;
                for (TableColumnTuple<String, String> required: tempNode.getRequired()) {
                    if (!allRequired.contains(required)) {
                        allRequired.add(required);
                    }
                }
            }
            else if (curNode instanceof HavingNode) {
                HavingNode tempNode = (HavingNode) curNode;
                for (TableColumnTuple<String, String> required: tempNode.getRequired()) {
                    if (!allRequired.contains(required)) {
                        allRequired.add(required);
                    }
                }
            }
            else if (curNode instanceof JoinNode) {
                JoinNode tempNode = (JoinNode) curNode;
                for (TableColumnTuple<String, String> required: tempNode.getRequired()) {
                    if (!allRequired.contains(required)) {
                        allRequired.add(required);
                    }
                }
                ScanNode tempScanNode = (ScanNode) tempNode.getRight();
                for (TableColumnTuple<String, String> required: allRequired) {
                    if (required.tableName.equals(tempScanNode.getTableName())) {
                        tempScanNode.addTargetColumn(required.columnName);
                    }
                }
            }
            else if (curNode instanceof ProjectNode) {
                ProjectNode tempNode = (ProjectNode) curNode;
                for (TableColumnTuple<String, String> required: tempNode.getRequired()) {
                    if (!allRequired.contains(required)) {
                        allRequired.add(required);
                    }
                }
            }
            else if (curNode instanceof SortNode) {
                SortNode tempNode = (SortNode) curNode;
                for (TableColumnTuple<String, String> required: tempNode.getRequired()) {
                    if (!allRequired.contains(required)) {
                        allRequired.add(required);
                    }
                }
            }
            curNode = curNode.getChild();
        }
        ScanNode tempScanNode = (ScanNode) curNode;
        for (TableColumnTuple<String, String> required: allRequired) {
            if (required.tableName.equals(tempScanNode.getTableName())) {
                tempScanNode.addTargetColumn(required.columnName);
            }
        }
    }

}