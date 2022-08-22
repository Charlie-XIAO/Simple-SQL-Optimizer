package CBO;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import plan.JoinNode;

public class JoinPredicates {
 
    private Set<List<String>> predicates;

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

    public Set<List<String>> getPredicates() {
        return predicates;
    }

    public void addPredicate(JoinNode joinNode) {
        String leftInfo = joinNode.getTableNameLeft() + "." + joinNode.getColumnNameLeft();
        String rightInfo = joinNode.getTableNameRight() + "." + joinNode.getTableNameRight();
        if (predicates.size() == 0) {
            List<String> predicateChain = new LinkedList<>();
            predicateChain.add(leftInfo);
            predicateChain.add(rightInfo);
            predicates.add(predicateChain);
        }
        else {
            for (List<String> predicateChain: predicates) {
                if (predicateChain.contains(leftInfo)) {
                    predicateChain.add(rightInfo);
                    return;
                }
                else if (predicateChain.contains(rightInfo)) {
                    predicateChain.add(leftInfo);
                    return;
                }
            }
            List<String> predicateChain = new LinkedList<>();
            predicateChain.add(leftInfo);
            predicateChain.add(rightInfo);
            predicates.add(predicateChain);
        }
    }

}