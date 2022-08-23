package table.Data;

public class BooleanData extends Data {
    
    private boolean value;

    public BooleanData(String value) {
        this.value = Boolean.parseBoolean(value);
    }

    public boolean getValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public String toString() {
        return String.format("%s", value);
    }
    
}
