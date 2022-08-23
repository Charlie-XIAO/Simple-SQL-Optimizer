package table.Data;

public class CharData extends Data {

    private char value;

    public CharData(String value) {
        this.value = value.charAt(0);
    }

    public char getValue() {
        return value;
    }

    public void setValue(char value) {
        this.value = value;
    }

    @Override
    public String getEvalExpression() {
        return Character.toString(value);
    }

    public String toString() {
        return String.format("%c", value);
    }
    
}
