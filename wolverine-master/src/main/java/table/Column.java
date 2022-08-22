package table;

import java.util.Locale;

public class Column {
    private String colName;
    private ColumnType colType;

    public Column(String colName, ColumnType colType) {
        this.colName = colName;
        this.colType = colType;
    }

    public Column(String colName, String colType) {
        this.colName = colName;
        this.colType = (colType == null) ? ColumnType.UNKNOWN : ColumnType.valueOf(colType.toUpperCase(Locale.ENGLISH));
    }

    public String toString() {
        return String.format("%s %s", colName, colType);
    }

    public String getColName() {
        return colName;
    }

    public ColumnType getColType() {
        return colType;
    }
}
