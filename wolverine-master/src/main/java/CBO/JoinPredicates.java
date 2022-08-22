package CBO;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map;

import plan.JoinNode;

public class JoinPredicates {
 
    private Set<Map<String, String>> predicates;

    public JoinPredicates() {
        this.predicates = new HashSet<>();
    }

    public JoinPredicates(JoinNode joinNode) {
        this.predicates = new HashSet<>();
        this.addPredicate(joinNode);
    }

    public JoinPredicates(Set<JoinNode> joinNodes) {
        this.predicates = new HashSet<>();
        for (JoinNode joinNode: joinNodes) {
            this.addPredicate(joinNode);
        }
    }
    public JoinPredicates(List<JoinNode> joinNodes) {
        this.predicates = new HashSet<>();
        for (JoinNode joinNode: joinNodes) {
            this.addPredicate(joinNode);
        }
    }

    public Set<Map<String, String>> getPredicates() {
        return predicates;
    }

    public void addPredicate(JoinNode joinNode) {
        for (Map<String, String> predicateChain: predicates) {
            String leftColumnNameInfo = predicateChain.get(joinNode.getTableNameLeft());
            String rightColumnNameInfo = predicateChain.get(joinNode.getTableNameRight());
            if (leftColumnNameInfo != null && leftColumnNameInfo.equals(joinNode.getColumnNameLeft())) {
                predicateChain.put(joinNode.getTableNameRight(), joinNode.getColumnNameRight());
                return;
            }
            else if (rightColumnNameInfo != null && rightColumnNameInfo.equals(joinNode.getColumnNameRight())) {
                predicateChain.put(joinNode.getTableNameLeft(), joinNode.getColumnNameLeft());
                return;
            }
        }
        Map<String, String> predicateChain = new HashMap<>();
        predicateChain.put(joinNode.getTableNameLeft(), joinNode.getColumnNameLeft());
        predicateChain.put(joinNode.getTableNameRight(), joinNode.getColumnNameRight());
        predicates.add(predicateChain);
    }

}