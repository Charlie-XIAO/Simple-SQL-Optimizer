package table.Data;

public class LongData extends Data {
    
    private long value;

    public LongData(String value) {
        this.value = Long.parseLong(value);
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    @Override
    public String getEvalExpression() {
        return Long.toString(value);
    }

    public String toString() {
        return String.format("%d", value);
    }

}
