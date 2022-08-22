package plan;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import table.Record;
import utils.BackTracingIterator;
import utils.ListBacktracingIterator;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

public class ScanNode extends Node {
    String tableName;

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }

    public String toString() {
        return "Scan(" + tableName + ")";
    }

    @Override
    public BackTracingIterator<Record> iterator() {
        return backTracingIterator();
    }

    public BackTracingIterator<Record> backTracingIterator() {
        ReadCSV();
        return new ListBacktracingIterator(records);
    }

    private void ReadCSV() {
        String fileLoc = String.format("./wolverine-master/data/%s.csv", tableName);
        try {
            Reader in = new FileReader(fileLoc);
            CSVFormat csvFormat = CSVFormat.DEFAULT.withHeader();
            CSVParser csvRecords = csvFormat.parse(in);
            List<String> headers = csvRecords.getHeaderNames();
            for (String header : headers) {
                String colName = header.split(" ")[0];
                String colType = header.split(" ")[1];
                table.addField(colName, colType);
            }
            for (CSVRecord csvRecord : csvRecords) {
                Record record = new Record(csvRecord, table.getSchema());
                records.add(record);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
