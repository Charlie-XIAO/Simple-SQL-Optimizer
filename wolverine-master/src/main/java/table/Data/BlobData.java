package table.Data;

import java.sql.Blob;
import javax.sql.rowset.serial.SerialBlob;

public class BlobData extends Data {

    private Blob value;

    public BlobData(String value) {
        try {
            this.value = new SerialBlob(value.getBytes());
        }
        catch (Exception e) {
            this.value = null;
        }
    }

    public Blob getValue() {
        return null;
    }

    public void setValue(Blob value) {
        this.value = value;
    }

    @Override
    public String getEvalExpression() {
        return value.toString();
    }

    public String toString() {
        return String.format("%s", value);
    }
    
}
