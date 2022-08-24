package utils;

import table.Record;
import plan.JoinNode;

public class HashJoinIterator implements BackTracingIterator<Record> {

    public HashJoinIterator(JoinNode joinNode, boolean isLeftOutside) {}

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public Record next() {
        return null;
    }

    @Override
    public void markPrev() {

    }

    @Override
    public void markNext() {

    }

    @Override
    public void reset() {

    }

    @Override
    public void markStart() {

    }

}