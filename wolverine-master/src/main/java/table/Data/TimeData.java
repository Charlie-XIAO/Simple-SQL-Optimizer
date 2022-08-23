package table.Data;

import java.sql.Time;

/**
 * Time data is in fact saved as a long integer.
 * It counts the milliseconds since January 1, 1970, 00:00:00 GMT.
 * A negative number is the number of milliseconds before January 1, 1970, 00:00:00 GMT.
 */

public class TimeData extends Data {
    
    private Time value;
    private long milliseconds;

    public TimeData(String value) {
        this.milliseconds = Long.parseLong(value);
        this.value = new Time(milliseconds);
    }

    public Time getValue() {
        return value;
    }

    public long getMilliseconds() {
        return milliseconds;
    }

    public void setValue(Time value) {
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
