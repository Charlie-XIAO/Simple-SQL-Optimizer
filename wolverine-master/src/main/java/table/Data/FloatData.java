package table.Data;

public class FloatData extends Data {
    private float value;

    public FloatData(String value) {
        this.value = Float.parseFloat(value);
    }

    public float getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String toString() {
        return String.format("%f", value);
    }
}
