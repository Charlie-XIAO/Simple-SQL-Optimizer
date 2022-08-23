package table.Data;

import java.sql.Timestamp;

/**
 * Timestamp data is in fact saved as long integer.
 * It counts the milliseconds since January 1, 1970, 00:00:00 GMT.
 * A negative number is the number of milliseconds before January 1, 1970, 00:00:00 GMT.
 */

public class TimestampData extends Data {
    
    private Timestamp value;
    private long milliseconds;

    public TimestampData(String value) {
        this.milliseconds = Long.parseLong(value);
        this.value = new Timestamp(milliseconds);
    }

    public Timestamp getValue() {
        return value;
    }

    public long getMilliseconds() {
        return milliseconds;
    }

    public void setValue(Timestamp value) {
        this.value = value;
    }

    @Override
    public String getEvalExpression() {
        return Long.toString(milliseconds);
    }

    public String toString() {
        return String.format("%t", value);
    }

}
