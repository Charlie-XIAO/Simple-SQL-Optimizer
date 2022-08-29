package RBO;

import plan.Node;
import plan.FilterNode;

public class RuleBasedOptimizer {
    
    private Node root;

    public RuleBasedOptimizer(Node root) {
        this.root = root;
    }

    public Node getOptimizedPlan() {
        new LimitPushDown(root).optimize();  // push down limits
        FilterNode filterNode = getFilterNode();
        new FilterPushDown(filterNode).optimize();  // push down filters
        Node filterParent = filterNode.getParent();
        filterParent.setChild(filterNode.getChild());  // remove original filter
        return root;
    }

    private FilterNode getFilterNode() {
        Node curNode = root;
        while (!curNode.isLeaf() && !(curNode instanceof FilterNode)) {
            curNode = curNode.getChild();
        }
        if (curNode instanceof FilterNode) {
            return (FilterNode) curNode;
        }
        else {
            return null;
        }
    }

}