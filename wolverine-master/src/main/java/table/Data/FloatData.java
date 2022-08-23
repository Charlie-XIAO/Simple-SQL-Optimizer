package table.Data;

public class FloatData extends Data {

    private float value;

    public FloatData(String value) {
        this.value = Float.parseFloat(value);
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    @Override
    public String getEvalExpression() {
        return Float.toString(value);
    }

    public String toString() {
        return String.format("%f", value);
    }

}