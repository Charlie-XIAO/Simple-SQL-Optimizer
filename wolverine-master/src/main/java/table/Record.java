package table;

import org.apache.commons.csv.CSVRecord;
import table.Data.*;

import java.util.ArrayList;
import java.util.List;

public class Record {
    
    private List<Data> data = new ArrayList<>();
    private List<Column> schema;

    public Record() {}

    public Record(CSVRecord csvRecord, List<Column> schema) {
        this.schema = schema;
        for (int i = 0; i < schema.size(); i ++) {
            ColumnType colType = schema.get(i).getColType();
            String originData = csvRecord.get(i);
            if (originData == "") {
                data.add(new NullData());
                continue;
            }
            switch (colType) {
                case BINARY:
                    data.add(new BinaryData(originData));
                    break;
                case BLOB:
                    data.add(new BlobData(originData));
                    break;
                case BOOLEAN:
                    data.add(new BooleanData(originData));
                    break;
                case CHAR:
                    data.add(new CharData(originData));
                    break;
                case DATE:
                    data.add(new DateData(originData));
                    break;
                case DOUBLE:
                    data.add(new DoubleData(originData));
                    break;
                case FLOAT:
                    data.add(new FloatData(originData));
                    break;
                case INT:
                    data.add(new IntData(originData));
                    break;
                case LONG:
                    data.add(new LongData(originData));
                    break;
                case STRING:
                    data.add(new StringData(originData));
                    break;
                case TEXT:
                    data.add(new TextData(originData));
                    break;
                case TIME:
                    data.add(new TimeData(originData));
                    break;
                case TIMESTAMP:
                    data.add(new TimestampData(originData));
                    break;
                case VARCHAR:
                    data.add(new VarcharData(originData));
                    break;
                default:
                    break;
            }
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < data.size(); i ++) {
            sb.append(data.get(i).toString());
            if (i != data.size() - 1) {
                sb.append(" | ");
            }
        }
        return sb.toString();
    }

    public List<Data> getData() {
        return data;
    }

    public Record concat(Record record) {
        Record newRecord = new Record();
        newRecord.getData().addAll(this.getData());
        newRecord.getData().addAll(record.getData());
        return newRecord;
    }
}
