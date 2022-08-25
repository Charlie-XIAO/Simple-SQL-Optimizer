package table;

import java.util.Locale;

public class Column {
    
    private String tableName;
    private String colName;
    private ColumnType colType;
    private int colSize;

    public Column(String tableName, String colName, ColumnType colType, int colSize) {
        this.tableName = tableName;
        this.colName = colName;
        this.colType = colType;
        this.colSize = colSize;
    }

    public Column(String tableName, String colName, String colType) {
        this.tableName = tableName;
        this.colName = colName;
        if (colType == null) {
            this.colType = ColumnType.UNKNOWN;
            this.colSize = 0;
        }
        else {
            if (colType.contains("(")) {
                try {
                    this.colType = ColumnType.valueOf(colType.substring(0, colType.indexOf("(")).toUpperCase(Locale.ENGLISH));
                    this.colSize = Integer.parseInt(colType.substring(colType.indexOf("(") + 1, colType.indexOf(")")));
                }
                catch (Exception e) {
                    System.out.println(e);
                    this.colType = ColumnType.UNKNOWN;
                    this.colSize = 0;
                }
            }
            else {
                this.colType = ColumnType.valueOf(colType.toUpperCase(Locale.ENGLISH));
                this.colSize = ColumnType.getSize(this.colType);
            }
        }
    }

    public String toString() {
        return String.format("%s %s(%d)", colName, colType, colSize);
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }

    public String getColName() {
        return colName;
    }

    public ColumnType getColType() {
        return colType;
    }

    public int getColSize() {
        return colSize;
    }

}
