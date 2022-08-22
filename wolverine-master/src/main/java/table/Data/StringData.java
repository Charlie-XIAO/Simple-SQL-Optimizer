package table.Data;

public class StringData extends Data {
    private String value;

    public StringData(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String toString() {
        return String.format("%s", value);
    }
}
