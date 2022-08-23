package table.Data;

public class DoubleData extends Data {
    
    private double value;

    public DoubleData(String value) {
        this.value = Double.parseDouble(value);
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String toString() {
        return String.format("%f", value);
    }

}
