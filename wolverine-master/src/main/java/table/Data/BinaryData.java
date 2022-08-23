package table.Data;

// TODO: Binary data is not this simple, need further modifications.
public class BinaryData extends Data {
    
    private String value;

    public BinaryData(String value) {
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
