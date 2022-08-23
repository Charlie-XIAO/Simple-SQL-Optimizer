package table;

public enum ColumnType {
    INT,
    STRING,
    DOUBLE,
    LONG,
    FLOAT,
    BOOLEAN,
    DATE,
    TIME,
    TIMESTAMP,
    VARCHAR,
    CHAR,
    BLOB,
    TEXT,
    BINARY,
    UNKNOWN;

    public static int getSize(ColumnType columnType) {
        if (columnType == BOOLEAN || columnType == CHAR) {
            return 1;
        }
        else if (columnType == DATE || columnType == TIME) {
            return 3;
        }
        else if (columnType == INT || columnType == FLOAT || columnType == TIMESTAMP) {
            return 4;
        }
        else if (columnType == DOUBLE || columnType == LONG) {
            return 8;
        }
        else if (columnType == STRING || columnType == VARCHAR) {
            return 2048;
        }
        else if (columnType == BINARY) {
            return 8000;
        }
        else if (columnType == BLOB || columnType == TEXT) {
            return 65536;
        }
        else {
            return 0;
        }
    }

}