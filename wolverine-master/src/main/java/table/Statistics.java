package table;

import java.util.List;

public class Statistics {

    private int rowCount;
    private List<Integer> columnSizes;  

    private List<Double> maxs;
    private List<Double> mins;
    private List<Integer> numNulls;

    public Statistics() {}

    public Statistics(int rowCount, List<Integer> columnSizes, List<Double> maxs, List<Double> mins, List<Integer> numNulls) {
        this.rowCount = rowCount;
        this.columnSizes = columnSizes;
        this.maxs = maxs;
        this.mins = mins;
        this.numNulls = numNulls;
    }

    public int getRowCount() {
        return rowCount;
    }

    public int getSize() {
        int size = 0;
        for (int columnSize: columnSizes) {
            size += columnSize;
        }
        return size;
    }

    public List<Integer> getColumnSizes() {
        return columnSizes;
    }

    public List<Double> getMaxs() {
        return maxs;
    }

    public List<Double> getMins() {
        return mins;
    }

    public List<Integer> getNumNulls() {
        return numNulls;
    }

    public String toString() {
        return "Statistics:\n"
            + "| Row count: " + rowCount + "\n"
            + "| Column sizes: " + columnSizes+ "\n"
            + "| Maximums: " + maxs + "\n"
            + "| Minimums: " + mins + "\n"
            + "| Null count: " + numNulls;
    }

}