package CBO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Collections;

import plan.Node;
import plan.FilterNode;
import plan.JoinNode;
import plan.OutputNode;
import plan.ScanNode;
import plan.type.JoinType;

public class JoinReorder {

    private OutputNode originalLogicalPlan;
    private boolean hasJoin;
    private List<List<JoinNode>> partitionedJoinNodes;

    public JoinReorder(OutputNode originalLogicalPlan) {
        this.originalLogicalPlan = originalLogicalPlan;
        this.partitionJoinNodes();
        //getBestJoinPlan(getPartitionRelations(partitionedJoinNodes.get(1)));
    }

    private void partitionJoinNodes() {
        Node curNode = originalLogicalPlan;
        List<List<JoinNode>> partitionedJoinNodes = new ArrayList<>();
        while (!curNode.isLeaf()) {
            if (curNode instanceof JoinNode) {
                JoinNode joinNode = (JoinNode) curNode;
                if (partitionedJoinNodes.size() == 0) {
                    List<JoinNode> partition = new ArrayList<>();
                    partition.add(joinNode);
                    partitionedJoinNodes.add(partition);
                } else {
                    List<JoinNode> prevPartition = partitionedJoinNodes.get(partitionedJoinNodes.size() - 1);
                    if (joinNode.getJoinType() == prevPartition.get(0).getJoinType()) {
                        prevPartition.add(joinNode);
                    } else {
                        List<JoinNode> partition = new ArrayList<>();
                        partition.add(joinNode);
                        partitionedJoinNodes.add(partition);
                    }
                }
            }
            curNode = curNode.getChild();
        }
        this.partitionedJoinNodes = partitionedJoinNodes;
        if (partitionedJoinNodes.size() == 0) {
            this.hasJoin = false;
        } else {
            this.hasJoin = true;
        }
    }

    public boolean hasJoin() {
        return hasJoin;
    }

    public List<List<JoinNode>> getPartitionedJoinNodes() {
        return partitionedJoinNodes;
    }

    private static Map<Integer, List<Set<Node>>> generateSubsets(List<Node> nodes) {
        Map<Integer, List<Set<Node>>> results = new HashMap<>();
        for (int i = 0; i < nodes.size(); i++) {
            results.put(i, new ArrayList<>());
        }
        generateSubsets(nodes, 0, new HashSet<Node>(), results);
        return results;
    }

    private static void generateSubsets(List<Node> nodes, int index, Set<Node> current,
                                        Map<Integer, List<Set<Node>>> results) {
        if (current.size() >= nodes.size()) {
            return;
        }
        List<Set<Node>> result = results.get(current.size());
        if (!result.contains(current)) {
            result.add(new HashSet<>(current));
        }
        if (index == nodes.size()) {
            return;
        }
        Node node = nodes.get(index);
        // recursive case: assume that node is present in the subset
        current.add(node);
        generateSubsets(nodes, index + 1, current, results);
        // recursive case: assume that node is not present in the subset
        current.remove(node);
        generateSubsets(nodes, index + 1, current, results);
    }

    private static List<Node> getPartitionRelations(List<JoinNode> partition) {
        List<Node> relations = new ArrayList<>();
        for (int i = 0; i < partition.size(); i++) {
            relations.add(partition.get(i).getRight());
            if (i == partition.size() - 1) {
                relations.add(partition.get(i).getLeft());
            }
        }
        return relations;
    }

    private static JoinType getPartitionJoinType(List<JoinNode> partition) {
        return partition.get(0).getJoinType();
    }

    private static JoinPredicates getPartitionJoinPredicates(List<JoinNode> partition) {
        return new JoinPredicates(partition);
    }

    private static JoinNode getBestJoinPlan(List<Node> relations, JoinType joinType, JoinPredicates joinPredicates) {
        Map<Integer, List<Set<Node>>> subsets = generateSubsets(relations);
        Map<Set<Node>, Node> planMap = new HashMap<>();
        for (Set<Node> combination: subsets.get(1)) {
            planMap.put(combination, combination.iterator().next());
        }
        for (int planSize = 2; planSize <= relations.size(); planSize++) {
            for (int leftSubplanSize = 1; leftSubplanSize < planSize; leftSubplanSize++) {
                for (Set<Node> leftSubplanCombination: subsets.get(leftSubplanSize)) {
                    for (Set<Node> rightSubplanCombination: subsets.get(planSize - leftSubplanSize)) {
                        if (Collections.disjoint(leftSubplanCombination, rightSubplanCombination)) {
                            Node leftSubplan = planMap.get(leftSubplanCombination);
                            Node rightSubplan = planMap.get(rightSubplanCombination);
                            Set<String> leftContainedTables = getContained(leftSubplan);
                            Set<String> rightContainedTables = getContained(rightSubplan);
                            
                        }
                    }
                }
            }
        }
        return null;
        //return (JoinNode) planMap.get(new HashSet<>(relations));

    }

    private static Set<String> getContained(Node subplan) {
        Set<String> contained = new HashSet<>();
        if (subplan instanceof ScanNode) {
            contained.add(((ScanNode) subplan).getTableName());
        }
        else if (subplan instanceof FilterNode) {
            contained.addAll(((FilterNode) subplan).getTableNames());
        }
        else if (subplan instanceof JoinNode) {
            contained.addAll(((JoinNode) subplan).getContained());
        }
        return contained;
    }

}