package table;

import java.util.ArrayList;
import java.util.List;

public class Table {

    private String tableName;
    private List<Column> schema = new ArrayList<>();

    public String getTableName() {
        return tableName;
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

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setSchema(List<Column> schema) {
        this.schema = schema;
    }

    // add field and meanwhile returning the size of that field
    public int addField(String name, String type) {
        Column column = new Column(name, type);
        this.schema.add(column);
        return column.getColSize();
    }

    public int addField(String name, ColumnType type, int size) {
        Column column = new Column(name, type, size);
        this.schema.add(column);
        return column.getColSize();
    }

}
