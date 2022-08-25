package utils;

import table.Record;
import plan.JoinNode;

import java.util.List;
import java.util.ArrayList;

public class BlockNestedLoopJoinIterator implements BackTracingIterator<Record> {
    private BackTracingIterator<Record> outsideSource;
    private BackTracingIterator<Record> insideSource;
    private BackTracingIterator<Record> outsideBuffer;
    private BackTracingIterator<Record> outsideBufferBackup;
    private Record insideRecord;
    private Record nextRecord;
    private final int BUFFER_SIZE = 200;

    public BlockNestedLoopJoinIterator(JoinNode joinNode, boolean isLeftOutside) {
        BackTracingIterator<Record> leftIterator = joinNode.getLeft().backTracingIterator();
        BackTracingIterator<Record> rightIterator = joinNode.getRight().backTracingIterator();
        outsideSource = (isLeftOutside) ? leftIterator : rightIterator;
        insideSource = (isLeftOutside) ? rightIterator : leftIterator;

        List<Record> buffer = new ArrayList<Record>();
        for (int i = 0; i < BUFFER_SIZE; i ++) {
            if (outsideSource.hasNext()) {
                buffer.add(outsideSource.next());
            } else {
                break;
            }
        }
        outsideBuffer = new ListBacktracingIterator(buffer);
        insideRecord = (insideSource.hasNext()) ? insideSource.next() : null;
        insideSource.markStart();
        outsideBuffer.markStart();
        nextRecord = computeNextRecord();
    }

    private Record computeNextRecord() {
        if (insideRecord == null) {
            return null;
        }
        while (true) {
            if (outsideBuffer.hasNext()) {
                return outsideBuffer.next().concat(insideRecord);
            } else if (insideSource.hasNext()) {
                insideRecord = insideSource.next();
                outsideBuffer.reset();
            } else if (outsideSource.hasNext()) {
                List<Record> buffer = new ArrayList<Record>();
                for (int i = 0; i < BUFFER_SIZE; i ++) {
                    if (outsideSource.hasNext()) {
                        buffer.add(outsideSource.next());
                    } else {
                        break;
                    }
                }
                outsideBuffer = new ListBacktracingIterator(buffer);
                insideSource.reset();
                insideRecord = insideSource.next();
            } else {
                return null;
            }
        }
    }

    @Override
    public boolean hasNext() {
        return nextRecord != null;
    }

    @Override
    public Record next() {
        if (!hasNext()) {
            throw new java.util.NoSuchElementException();
        }
        Record result = nextRecord;
        nextRecord = computeNextRecord();
        return result;
    }

    @Override
    public void markPrev() {
        outsideSource.markPrev();
        insideSource.markPrev();
        outsideBufferBackup = outsideBuffer;
        outsideBufferBackup.markPrev();
    }

    @Override
    public void markNext() {
        outsideSource.markNext();
        insideSource.markNext();
        outsideBufferBackup = outsideBuffer;
        outsideBufferBackup.markNext();
    }

    @Override
    public void reset() {
        outsideSource.reset();
        insideSource.reset();
        outsideBuffer = outsideBufferBackup;
        outsideBuffer.reset();
    }

    @Override
    public void markStart() {
        outsideSource.markStart();
        insideSource.markStart();
        outsideBuffer = outsideBufferBackup;
        outsideBuffer.markStart();
    }
}