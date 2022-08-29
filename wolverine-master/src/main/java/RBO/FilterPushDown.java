package RBO;

import plan.FilterNode;
import plan.Node;
import plan.ScanNode;
import plan.JoinNode;

public class FilterPushDown {
    
    private FilterNode filterNode;

    public FilterPushDown(FilterNode filterNode) {
        this.filterNode = filterNode;
    }

    @Deprecated
    public Node getOptimizedPlan() {
        optimize();
        Node filterParent = filterNode.getParent();
        filterParent.setChild(filterNode.getChild());
        return filterParent;
    }

    protected void optimize() {
        Node curNode = (Node) this.filterNode;
        ScanNode scanNode;
        FilterNode subFilterNode;
        while (!curNode.isLeaf()) {
            if (curNode instanceof JoinNode) {
                scanNode = (ScanNode) ((JoinNode) curNode).getRight();
                subFilterNode = this.filterNode.selectByTableName(scanNode.getTableName());
                if (subFilterNode.getItemCount() != 0) {
                    ((JoinNode) curNode).setRight(subFilterNode);
                    subFilterNode.setChild(scanNode);
                }
            }
            curNode = curNode.getChild();
        }
        if (curNode instanceof ScanNode) {
            scanNode = (ScanNode) curNode;
            subFilterNode = this.filterNode.selectByTableName(scanNode.getTableName());
            if (subFilterNode.getItemCount() != 0) {
                ((JoinNode) scanNode.getParent()).setLeft(subFilterNode);
                subFilterNode.setChild(scanNode);
            }
        }
    }

}