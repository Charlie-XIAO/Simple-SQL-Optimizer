package RBO;

import plan.JoinNode;
import plan.LimitNode;
import plan.Node;
import plan.ProjectNode;
import plan.SortNode;

public class LimitPushDown {

    private Node root;
    private boolean isLimitPushedDown;

    public LimitPushDown(Node root) {
        this.root = root;
        this.isLimitPushedDown = false;
    }

    public Node getOptimizedPlan() {
        optimize(root);
        return this.root;
    }

    public void optimize(Node node) {
        pushDownLimit(node);
        if (node.isJoinNode()) {
            optimize(((JoinNode) node).getLeft());
            optimize(((JoinNode) node).getRight());
        }
        else {
            if (node.getChild() != null) {
                optimize(node.getChild());
            }
        }
    }

    private void pushDownLimit(Node node) {
        if (node == null) {
            return;
        }
        Node child = node.getChild();
        if (child != null && child instanceof LimitNode) {
            if (!isLimitPushedDown) {
                isLimitPushedDown = true;
                LimitNode limitNode = (LimitNode) child;
                Node projectNode = limitNode.getChild();
                while (!(projectNode instanceof ProjectNode)) {
                    projectNode = projectNode.getChild();
                }
                if (limitNode.getChild() instanceof SortNode) {
                    SortNode sortNode = (SortNode) limitNode.getChild();
                    node.setChild(sortNode.getChild());
                    sortNode.setChild(projectNode.getChild());
                    projectNode.setChild(limitNode);
                }
                else {
                    node.setChild(limitNode.getChild());
                    limitNode.setChild(projectNode.getChild());
                    projectNode.setChild(limitNode);
                }
            }
        }
    }
}
