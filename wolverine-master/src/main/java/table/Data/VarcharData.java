package table.Data;

public class VarcharData extends Data {

    private String value;

    public VarcharData(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String getEvalExpression() {
        return value;
    }

    public String toString() {
        return String.format("%s", value);
    }
    
}
