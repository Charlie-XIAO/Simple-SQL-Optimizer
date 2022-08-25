package plan;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import table.Record;
import table.Data.*;
import table.Statistics;
import table.Table;
import table.Column;
import utils.BackTracingIterator;
import utils.ListBacktracingIterator;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

import java.util.ArrayList;
import java.util.Iterator;

public class ScanNode extends Node {

    String tableName;
    // for data storage
    public Table table = new Table();
    public List<Record> records = new ArrayList<>();
    // CBO required statistics
    Statistics statistics = new Statistics();

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }

    public Statistics getStatistics() {
        return statistics;
    }

    public String toString() {
        return "Scan(" + tableName + ")";
    }

    @Override
    public Iterator<Record> iterator() {
        return backTracingIterator();
    }

    public BackTracingIterator<Record> backTracingIterator() {
        readCSV();
        return new ListBacktracingIterator(records);
    }

    private void readCSV() {
        int rowCount = 0;
        List<Integer> columnSizes = new ArrayList<>();
        List<Double> mins = new ArrayList<>();
        List<Double> maxs = new ArrayList<>();
        List<Integer> numNulls = new ArrayList<>();
        String fileLoc = String.format("./wolverine-master/data/%s.csv", tableName);
        table.addTableName(tableName);
        try {
            Reader in = new FileReader(fileLoc);
            CSVFormat csvFormat = CSVFormat.DEFAULT.withHeader();
            CSVParser csvRecords = csvFormat.parse(in);
            List<String> headers = csvRecords.getHeaderNames();
            for (String header: headers) {
                String colName = header.split(" ")[0];
                String colType = header.split(" ")[1];
                columnSizes.add(table.addField(tableName, colName, colType));
            }
            for (CSVRecord csvRecord: csvRecords) {
                Record record = new Record(csvRecord, table.getSchema());
                for (int i = 0; i < record.getData().size(); i ++) {
                    Data data = record.getData().get(i);
                    Double curData;
                    if (data instanceof DoubleData) {
                        curData = ((DoubleData) data).getValue();
                    }
                    else if (data instanceof FloatData) {
                        curData = (double) ((FloatData) data).getValue();
                    }
                    else if (data instanceof IntData) {
                        curData = (double) ((IntData) data).getValue();
                    }
                    else if (data instanceof LongData) {
                        curData = (double) ((LongData) data).getValue();
                    }
                    else if (data instanceof DateData) {
                        curData = (double) ((DateData) data).getMilliseconds();
                    }
                    else if (data instanceof TimeData) {
                        curData = (double) ((TimeData) data).getMilliseconds();
                    }
                    else if (data instanceof TimestampData) {
                        curData = (double) ((TimestampData) data).getMilliseconds();
                    }
                    else if (data instanceof CharData) {
                        curData = (double) ((CharData) data).getValue();
                    }
                    else {
                        curData = null;
                    }
                    if (rowCount == 0) {
                        mins.add(curData);
                        maxs.add(curData);
                        if (data instanceof NullData) {
                            numNulls.add(1);
                        }
                        else {
                            numNulls.add(0);
                        }
                    }
                    else {
                        if (data instanceof NullData) {
                            numNulls.set(i, numNulls.get(i) + 1);
                        }
                        if (curData == null) {
                            continue;
                        }
                        if (curData < mins.get(i)) {
                            mins.set(i, curData);
                        }
                        if (curData > maxs.get(i)) {
                            maxs.set(i, curData);
                        }
                    }
                }
                records.add(record);
                rowCount ++;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        this.statistics = new Statistics(rowCount, columnSizes, mins, maxs, numNulls);
    }
    
}