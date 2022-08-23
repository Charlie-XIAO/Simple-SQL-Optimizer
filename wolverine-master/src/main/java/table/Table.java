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

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setSchema(List<Column> schema) {
        this.schema = schema;
    }

    // add field, meanwhile returning the size of that field
    public int addField(String name, String type) {
        Column column = new Column(name, type);
        this.schema.add(new Column(name, type));
        return column.getColSize();
    }
}

