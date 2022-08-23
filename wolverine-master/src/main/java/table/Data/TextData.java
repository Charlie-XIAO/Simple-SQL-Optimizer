package table.Data;

public class TextData extends Data {
    
    private String value;

    public TextData(String value) {
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
