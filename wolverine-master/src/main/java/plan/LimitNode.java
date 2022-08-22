package plan;

public class LimitNode extends Node {
    private int limit;

    public LimitNode(int limit) {
        this.limit = limit;
    }

    public String toString() {
        return "Limit(" + limit + ")";
    }
}


