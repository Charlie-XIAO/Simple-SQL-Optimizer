package table;

import org.apache.commons.csv.CSVRecord;
import table.Data.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Record {
    
    private List<Data> data = new ArrayList<>();
    private List<Column> schema;
    private boolean _used = false;  // for join node check

    public Record() {}

    public Record(int columns, List<Column> schema) {
        this.schema = schema;
        for (int i = 0; i < columns; i ++) {
            data.add(new NullData());
        }
    }

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

    public boolean used() {
        return _used;
    }

    public List<Column> getSchema() {
        return schema;
    }

    public List<String> getColumnNames() {
        List<String> columnNames = new ArrayList<>();
        for (Column column: schema) {
            columnNames.add(column.getColName());
        }
        return columnNames;
    }

    public List<String> getColumnNamesUpperCase() {
        List<String> columnNames = new ArrayList<>();
        for (Column column: schema) {
            columnNames.add(column.getColName().toUpperCase(Locale.ENGLISH));
        }
        return columnNames;
    }
    
    public void setSchema(List<Column> schema) {
        this.schema = schema;
    }

    public void markUsed() {
        this._used = true;
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
        List<Column> newSchema = new ArrayList<>(this.schema);
        newSchema.addAll(record.schema);
        newRecord.setSchema(newSchema);
        this.markUsed();
        record.markUsed();
        return newRecord;
    }

    public Record concatExcept(Record record, int index) {
        Record newRecord = new Record();
        newRecord.getData().addAll(this.getData());
        newRecord.getData().addAll(record.getData().subList(0, index));
        newRecord.getData().addAll(record.getData().subList(index + 1, record.getData().size()));
        List<Column> newSchema = new ArrayList<>(this.schema);
        newSchema.addAll(record.schema.subList(0, index));
        newSchema.addAll(record.schema.subList(index + 1, record.schema.size()));
        newRecord.setSchema(newSchema);
        this.markUsed();
        record.markUsed();
        return newRecord;
    }

}