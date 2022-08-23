package table.Data;

import java.sql.Date;

/**
 * Date data is in fact saved as a long integer.
 * It counts the milliseconds since January 1, 1970, 00:00:00 GMT.
 * A negative number is the number of milliseconds before January 1, 1970, 00:00:00 GMT.
 * If the millisecond date value contains time info, mask it out.
 */

public class DateData extends Data {
    
    private Date value;
    private long milliseconds;

    public DateData(String value) {
        this.milliseconds = Long.parseLong(value);
        this.value = new Date(milliseconds);
    }

    public Date getValue() {
        return value;
    }

    public long getMilliseconds() {
        return milliseconds;
    }

    public void setValue(Date value) {
        this.value = value;
    }

    public String toString() {
        return String.format("%t", value);
    }

}
