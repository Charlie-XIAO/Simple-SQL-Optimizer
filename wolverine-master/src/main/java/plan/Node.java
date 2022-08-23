package plan;

import table.Record;
import utils.BackTracingIterator;
import utils.BacktracingIterable;

import java.util.Iterator;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;

import java.util.List;

public class Node implements BacktracingIterable<Record> {
    
    protected int height;
    private Node parent;
    private Node child = null;

    public void setChild(Node child) {
        this.child = child;
        child.setParent(this);
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public Node getChild() {
        return this.child;
    }

    public Node getParent() {
        return this.parent;
    }

    public Map<Integer, List<Node>> getLogicalPlan() {
        Map<Integer, List<Node>> logicalPlan = new HashMap<>();
        getLogicalPlan(logicalPlan);
        return logicalPlan;
    }

    public boolean isLeaf() {
        return this.child == null;
    }

    public void getLogicalPlan(Map<Integer, List<Node>> logicalPlan) {
        if (!logicalPlan.containsKey(this.height)) {
            logicalPlan.put(this.height, new ArrayList<Node>());
        }
        logicalPlan.get(this.height).add(this);
        if (isJoinNode()) {
            ((JoinNode) this).getLeft().getLogicalPlan(logicalPlan);
            ((JoinNode) this).getRight().getLogicalPlan(logicalPlan);
        } else {
            if (this.getChild() != null) {
                this.getChild().getLogicalPlan(logicalPlan);
            }
        }
    }

    public boolean isJoinNode() {
        return false;
    }

    @Deprecated
    public void printLogicalPlan() {
        Map<Integer, List<Node>> logicalPlan = getLogicalPlan();
        for (int height: logicalPlan.keySet()) {
            System.out.print(height + ": ");
            for (Node node: logicalPlan.get(height)) {
                System.out.print(node + "   ");
            }
            System.out.println();
        }
    }

    public void printPlan() {
        printPlan(0);
    }

    private void printPlan(int blankNum) {
        String blank = "";
        for (int i = 0; i < blankNum; i ++) {
            blank += "-- ";
        }
        System.out.println(blank + this);
        if (this.isJoinNode()) {
            ((JoinNode) this).getLeft().printPlan(blankNum + 1);
            ((JoinNode) this).getRight().printPlan(blankNum + 1);
        } else {
            if (this.getChild() != null) {
                this.getChild().printPlan(blankNum + 1);
            }
        }
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getHeight() {
        return this.height;
    }

    @Override
    public Iterator<Record> iterator() {
        return null;
    }

    @Override
    public BackTracingIterator<Record> backTracingIterator() {
        return null;
    }
}
