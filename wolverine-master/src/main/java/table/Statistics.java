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
        return "______ ______ STATISTICS ______ ______\n"
            + "Row count: " + Integer.toString(rowCount) + "\n"
            + "Column sizes: " + columnSizes.toString() + "\n"
            + "Maximums: " + maxs.toString() + "\n"
            + "Minimums: " + mins.toString() + "\n"
            + "Null count: " + numNulls.toString();
    }

}