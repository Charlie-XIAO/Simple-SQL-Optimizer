package utils;

import java.io.Serializable;

public class TableColumnTuple<A, B> implements Serializable  {
    
    public A tableName;
    public B columnName;

    public TableColumnTuple(A tableName, B columnName) {
        this.tableName = tableName;
        this.columnName = columnName;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof TableColumnTuple)) {
            return false;
        }
        TableColumnTuple<A, B> tuple = (TableColumnTuple<A, B>) obj;
        return tuple.tableName.equals(tableName) && tuple.columnName.equals(columnName);
    }

    @Override
    public int hashCode() {
        return 0;
    }    

    @Override
    public String toString() {
        return tableName.toString() + "." + columnName.toString();
    }

}