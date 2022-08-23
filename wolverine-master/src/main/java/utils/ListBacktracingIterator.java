package utils;

import table.Record;

import java.util.List;

public class ListBacktracingIterator implements BackTracingIterator<Record> {

    int curIndex = 0;
    int endIndex = 0;
    int markIndex = 0;
    private List<Record> records;

    public ListBacktracingIterator(List<Record> records) {
        this.records = records;
        this.endIndex = records.size();
    }

    @Override
    public boolean hasNext() {
        return curIndex < endIndex;
    }

    @Override
    public Record next() {
        return records.get(curIndex ++);
    }

    @Override
    public void markPrev() {
        markIndex = curIndex - 1;
    }

    @Override
    public void markNext() {
        markIndex = curIndex;
    }

    @Override
    public void reset() {
        curIndex = markIndex;
    }

    @Override
    public void markStart() {
        markIndex = 0;
    }
}
