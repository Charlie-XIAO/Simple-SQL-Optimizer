package table;

import org.apache.commons.csv.CSVRecord;
import table.Data.Data;
import table.Data.FloatData;
import table.Data.IntData;
import table.Data.StringData;

import java.util.ArrayList;
import java.util.List;

public class Record {
    private List<Data> data = new ArrayList<>();
    private List<Column> schema;

    public Record(CSVRecord csvRecord, List<Column> schema) {
        this.schema = schema;
        for (int i = 0; i < schema.size(); i++) {
            ColumnType colType = schema.get(i).getColType();
            String originData = csvRecord.get(i);
            switch (colType) {
            case INT:
                data.add(new IntData(originData));
                break;
            case STRING:
                data.add(new StringData(originData));
                break;
            case FLOAT:
                data.add(new FloatData(originData));
            default:
                break;
            }
        }
    }

    public Record() {

    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < data.size(); i++) {
            sb.append(data.get(i).toString());
            if (i != data.size() - 1) {
                sb.append(", ");
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
