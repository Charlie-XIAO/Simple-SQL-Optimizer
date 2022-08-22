package plan;

public class AggregateFunc {

    private AggregateFuncType funcType;

    public AggregateFunc(AggregateFuncType funcType) {
        this.funcType = funcType == null ? AggregateFuncType.NONE : funcType;
    }

    public String toString() {
        return funcType.toString();
    }
}

enum AggregateFuncType {
    AVG, COUNT, MAX, MIN, SUM, NONE
}
