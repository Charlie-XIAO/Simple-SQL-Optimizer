package table;

import java.util.ArrayList;
import java.util.List;

public class Table {

    private List<String> tableNames = new ArrayList<>();
    private List<Column> schema = new ArrayList<>();

    public Table() {}

    public Table(List<String> tableNames) {
        this.tableNames = tableNames;
    }

    public List<String> getTableNames() {
        return tableNames;
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

    public void addTableName(String tableName) {
        this.tableNames.add(tableName);
    }

    public void setTableNames(List<String> tableNames) {
        this.tableNames = tableNames;
    }

    public void setSchema(List<Column> schema) {
        this.schema = schema;
    }

    public int addField(String tableName, String name, String type) {
        Column column = new Column(tableName, name, type);
        this.schema.add(column);
        return column.getColSize();
    }

    public int addField(String tableName, String name, ColumnType type, int size) {
        Column column = new Column(tableName, name, type, size);
        this.schema.add(column);
        return column.getColSize();
    }

    public int addTargetField(List<String> columns, String tableName, String name, String type) {
        Column column = new Column(tableName, name, type);
        System.out.println("Columns " + columns + " check if contains column " + column.getColName()); //////
        if (columns.contains(column.getColName())) {
            this.schema.add(column);
            return column.getColSize();
        }
        else {
            return -1;
        }
    }

    public int addTargetField(List<String> columns, String tableName, String name, ColumnType type, int size) {
        Column column = new Column(tableName, name, type, size);
        System.out.println("Columns " + columns + " check if contains column " + column.getColName()); //////
        if (columns.contains(column.getColName())) {
            this.schema.add(column);
            return column.getColSize();
        }
        else {
            return -1;
        }
    }

}
