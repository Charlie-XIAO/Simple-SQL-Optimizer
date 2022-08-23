package table.Data;

public class NullData extends Data {

    public Object getValue() {
        return null;
    }

    @Override
    public String getEvalExpression() {
        return "null";
    }

    public String toString() {
        return "null";
    }
    
}
