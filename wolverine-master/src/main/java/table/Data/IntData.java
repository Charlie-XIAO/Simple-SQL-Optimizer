package table.Data;

public class IntData extends Data {

    private int value;

    public IntData(String value) {
        this.value = Integer.parseInt(value);
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String toString() {
        return String.format("%d", value);
    }
    
}
