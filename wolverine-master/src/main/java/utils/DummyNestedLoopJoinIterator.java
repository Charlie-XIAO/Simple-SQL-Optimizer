package utils;

import plan.JoinNode;
import plan.type.JoinType;
import table.Record;
import table.Statistics;
import table.Column;
import table.Data.*;

import java.util.ArrayList;
import java.util.List;

public class DummyNestedLoopJoinIterator implements BackTracingIterator<Record> {

    private JoinNode joinNode;
    private JoinType joinType;

    private int rowCount = 0;
    private List<Integer> columnSizes = new ArrayList<>();
    private List<Double> mins = new ArrayList<>();
    private List<Double> maxs = new ArrayList<>();
    private List<Integer> numNulls = new ArrayList<>();

    private BackTracingIterator<Record> outsideSource;
    private BackTracingIterator<Record> insideSource;
    private Record outsideRecord;
    private Record nextRecord;
    private List<Column> joinedSchema;

    private int outsideIndex, insideIndex;
    private int outsideLength, insideLength;  // outer joins required null record length
    private List<Column> outsideSchema, insideSchema; // outer joins required null record schema
    private boolean terminate = false;  // outer joins required terminate condition
    private boolean removeDuplicate = false; // joins required remove duplicated column

    public DummyNestedLoopJoinIterator(JoinNode joinNode, boolean isLeftOutside) {
        this.joinNode = joinNode;
        this.joinType = joinNode.getJoinType();
        if (joinNode.getColumnNameLeft().equals(joinNode.getColumnNameRight())) {
            removeDuplicate = true;
        }
        BackTracingIterator<Record> leftIterator = joinNode.getLeft().backTracingIterator();
        BackTracingIterator<Record> rightIterator = joinNode.getRight().backTracingIterator();
        if (joinType == JoinType.INNER) {
            outsideSource = (isLeftOutside) ? leftIterator : rightIterator;
            insideSource = (isLeftOutside) ? rightIterator : leftIterator;
            outsideRecord = (outsideSource.hasNext()) ? outsideSource.next() : null;
            outsideIndex = (isLeftOutside) ? outsideRecord.getColumnNamesUpperCase().indexOf(joinNode.getColumnNameLeft()) : outsideRecord.getColumnNamesUpperCase().indexOf(joinNode.getColumnNameRight());
            insideSource.markStart();
            Record tempInsideRecord = insideSource.next();
            insideIndex = (isLeftOutside) ? tempInsideRecord.getColumnNamesUpperCase().indexOf(joinNode.getColumnNameRight()) : tempInsideRecord.getColumnNamesUpperCase().indexOf(joinNode.getColumnNameLeft());
            insideSource.reset();
            joinedSchema = new ArrayList<>(outsideRecord.getSchema());
            if (removeDuplicate) {
                joinedSchema.addAll(tempInsideRecord.getSchema().subList(0, insideIndex));
                joinedSchema.addAll(tempInsideRecord.getSchema().subList(insideIndex + 1, tempInsideRecord.getSchema().size()));
            }
            else {
                joinedSchema.addAll(tempInsideRecord.getSchema());
            }
            nextRecord = computeNextInnerRecord(outsideIndex, insideIndex);
        }
        else if (joinType == JoinType.LEFT) {
            outsideSource = (isLeftOutside) ? leftIterator : rightIterator;
            insideSource = (isLeftOutside) ? rightIterator : leftIterator;
            outsideRecord = (outsideSource.hasNext()) ? outsideSource.next() : null;
            outsideLength = outsideRecord.getData().size();
            outsideSchema = outsideRecord.getSchema();
            outsideIndex = (isLeftOutside) ? outsideRecord.getColumnNamesUpperCase().indexOf(joinNode.getColumnNameLeft()) : outsideRecord.getColumnNamesUpperCase().indexOf(joinNode.getColumnNameRight());
            insideSource.markStart();
            Record tempInsideRecord = insideSource.next();
            insideLength = tempInsideRecord.getData().size();
            insideSchema = tempInsideRecord.getSchema();
            insideIndex = (isLeftOutside) ? tempInsideRecord.getColumnNamesUpperCase().indexOf(joinNode.getColumnNameRight()) : tempInsideRecord.getColumnNamesUpperCase().indexOf(joinNode.getColumnNameLeft());
            insideSource.reset();
            joinedSchema = new ArrayList<>(outsideSchema);
            if (removeDuplicate) {
                joinedSchema.addAll(insideSchema.subList(0, insideIndex));
                joinedSchema.addAll(insideSchema.subList(insideIndex + 1, insideSchema.size()));
            }
            else {
                joinedSchema.addAll(insideSchema);
            }
            nextRecord = computeNextLeftRecord(outsideIndex, insideIndex);
        }
        else if (joinType == JoinType.RIGHT) {
            outsideSource = (isLeftOutside) ? rightIterator : leftIterator;
            insideSource = (isLeftOutside) ? leftIterator : rightIterator;
            outsideRecord = (outsideSource.hasNext()) ? outsideSource.next() : null;
            outsideLength = outsideRecord.getData().size();
            outsideSchema = outsideRecord.getSchema();
            outsideIndex = (isLeftOutside) ? outsideRecord.getColumnNamesUpperCase().indexOf(joinNode.getColumnNameRight()) : outsideRecord.getColumnNamesUpperCase().indexOf(joinNode.getColumnNameLeft());
            insideSource.markStart();
            Record tempInsideRecord = insideSource.next();
            insideLength = tempInsideRecord.getData().size();
            insideSchema = tempInsideRecord.getSchema();
            insideIndex = (isLeftOutside) ? tempInsideRecord.getColumnNamesUpperCase().indexOf(joinNode.getColumnNameLeft()) : tempInsideRecord.getColumnNamesUpperCase().indexOf(joinNode.getColumnNameRight());
            insideSource.reset();
            joinedSchema = new ArrayList<>(outsideSchema);
            if (removeDuplicate) {
                joinedSchema.addAll(insideSchema.subList(0, insideIndex));
                joinedSchema.addAll(insideSchema.subList(insideIndex + 1, insideSchema.size()));
            }
            else {
                joinedSchema.addAll(insideSchema);
            }
            nextRecord = computeNextLeftRecord(outsideIndex, insideIndex);
        }
        else if (joinType == JoinType.FULL) {
            outsideSource = (isLeftOutside) ? leftIterator : rightIterator;
            insideSource = (isLeftOutside) ? rightIterator : leftIterator;
            outsideRecord = (outsideSource.hasNext()) ? outsideSource.next() : null;
            outsideLength = outsideRecord.getData().size();
            outsideSchema = outsideRecord.getSchema();
            outsideIndex = (isLeftOutside) ? outsideRecord.getColumnNamesUpperCase().indexOf(joinNode.getColumnNameLeft()) : outsideRecord.getColumnNamesUpperCase().indexOf(joinNode.getColumnNameRight());
            insideSource.markStart();
            Record tempInsideRecord = insideSource.next();
            insideLength = tempInsideRecord.getData().size();
            insideSchema = tempInsideRecord.getSchema();
            insideIndex = (isLeftOutside) ? tempInsideRecord.getColumnNamesUpperCase().indexOf(joinNode.getColumnNameRight()) : tempInsideRecord.getColumnNamesUpperCase().indexOf(joinNode.getColumnNameLeft());
            insideSource.reset();
            joinedSchema = new ArrayList<>(outsideSchema);
            if (removeDuplicate) {
                joinedSchema.addAll(insideSchema.subList(0, insideIndex));
                joinedSchema.addAll(insideSchema.subList(insideIndex + 1, insideSchema.size()));
            }
            else {
                joinedSchema.addAll(insideSchema);
            }
            nextRecord = computeNextFullRecord(outsideIndex, insideIndex);
        }
        else {
            outsideSource = (isLeftOutside) ? leftIterator : rightIterator;
            insideSource = (isLeftOutside) ? rightIterator : leftIterator;
            outsideRecord = (outsideSource.hasNext()) ? outsideSource.next() : null;
            insideSource.markStart();
            Record tempInsideRecord = insideSource.next();
            insideSource.reset();
            joinedSchema = new ArrayList<>(outsideRecord.getSchema());
            if (removeDuplicate) {
                joinedSchema.addAll(tempInsideRecord.getSchema().subList(0, insideIndex));
                joinedSchema.addAll(tempInsideRecord.getSchema().subList(insideIndex + 1, tempInsideRecord.getSchema().size()));
            }
            else {
                joinedSchema.addAll(tempInsideRecord.getSchema());
            }
            nextRecord = computeNextRecord();
        }
        joinNode.setTableSchema(joinedSchema);
        joinNode.records.add(nextRecord);
        rowCount ++;
        for (int i = 0; i < nextRecord.getData().size(); i ++) {
            Data data = nextRecord.getData().get(i);
            Double curData;
            if (data instanceof DoubleData) {
                curData = ((DoubleData) data).getValue();
            }
            else if (data instanceof FloatData) {
                curData = (double) ((FloatData) data).getValue();
            }
            else if (data instanceof IntData) {
                curData = (double) ((IntData) data).getValue();
            }
            else if (data instanceof LongData) {
                curData = (double) ((LongData) data).getValue();
            }
            else if (data instanceof DateData) {
                curData = (double) ((DateData) data).getMilliseconds();
            }
            else if (data instanceof TimeData) {
                curData = (double) ((TimeData) data).getMilliseconds();
            }
            else if (data instanceof TimestampData) {
                curData = (double) ((TimestampData) data).getMilliseconds();
            }
            else if (data instanceof CharData) {
                curData = (double) ((CharData) data).getValue();
            }
            else {
                curData = null;
            }
            mins.add(curData);
            maxs.add(curData);
            if (data instanceof NullData) {
                numNulls.add(1);
            }
            else {
                numNulls.add(0);
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
        if (joinType == JoinType.INNER) {
            nextRecord = computeNextInnerRecord(outsideIndex, insideIndex);
        }
        else if (joinType == JoinType.LEFT || joinType == JoinType.RIGHT) {
            nextRecord = computeNextLeftRecord(outsideIndex, insideIndex);
        }
        else if (joinType == JoinType.FULL) {
            nextRecord = computeNextFullRecord(outsideIndex, insideIndex);
        }
        else {
            nextRecord = computeNextRecord();
        }
        if (nextRecord != null) {
            joinNode.records.add(nextRecord);
            rowCount ++;
            for (int i = 0; i < nextRecord.getData().size(); i ++) {
                Data data = nextRecord.getData().get(i);
                Double curData;
                if (data instanceof DoubleData) {
                    curData = ((DoubleData) data).getValue();
                }
                else if (data instanceof FloatData) {
                    curData = (double) ((FloatData) data).getValue();
                }
                else if (data instanceof IntData) {
                    curData = (double) ((IntData) data).getValue();
                }
                else if (data instanceof LongData) {
                    curData = (double) ((LongData) data).getValue();
                }
                else if (data instanceof DateData) {
                    curData = (double) ((DateData) data).getMilliseconds();
                }
                else if (data instanceof TimeData) {
                    curData = (double) ((TimeData) data).getMilliseconds();
                }
                else if (data instanceof TimestampData) {
                    curData = (double) ((TimestampData) data).getMilliseconds();
                }
                else if (data instanceof CharData) {
                    curData = (double) ((CharData) data).getValue();
                }
                else {
                    curData = null;
                }
                if (data instanceof NullData) {
                    numNulls.set(i, numNulls.get(i) + 1);
                }
                if (curData == null) {
                    continue;
                }
                if (curData < mins.get(i)) {
                    mins.set(i, curData);
                }
                if (curData > maxs.get(i)) {
                    maxs.set(i, curData);
                }
            }
        }
        else {
            for (Column column: joinedSchema) {
                columnSizes.add(column.getColSize());
            }
            joinNode.setStatistics(new Statistics(rowCount, columnSizes, mins, maxs, numNulls));
        }
        return result;
    }

    private Record computeNextRecord() {
        if (outsideRecord == null) {
            return null;
        }
        while (true) {
            if (insideSource.hasNext()) {
                Record insideRecord = insideSource.next();
                return concat(outsideRecord, insideRecord);
            }
            else if (outsideSource.hasNext()) {
                outsideRecord = outsideSource.next();
                insideSource.reset();
            }
            else {
                return null;
            }
        }
    }

    private Record computeNextInnerRecord(int outsideIndex, int insideIndex) {
        if (outsideRecord == null) {
            return null;
        }
        while (true) {
            if (insideSource.hasNext()) {
                Record insideRecord = insideSource.next();
                if (insideRecord.getData().get(insideIndex).getEvalExpression().equals(outsideRecord.getData().get(outsideIndex).getEvalExpression())) {
                    return concat(outsideRecord, insideRecord);
                }
            }
            else if (outsideSource.hasNext()) {
                outsideRecord = outsideSource.next();
                insideSource.reset();
            }
            else {
                return null;
            }
        }
    }

    private Record computeNextLeftRecord(int outsideIndex, int insideIndex) {
        if (outsideRecord == null || terminate) {
            return null;
        }
        while (true) {
            if (insideSource.hasNext()) {
                Record insideRecord = insideSource.next();
                if (insideRecord.getData().get(insideIndex).getEvalExpression().equals(outsideRecord.getData().get(outsideIndex).getEvalExpression())) {
                    outsideRecord.markUsed();
                    return concat(outsideRecord, insideRecord);
                }
            }
            else if (outsideSource.hasNext() && !outsideRecord.used()) {
                Record result = concat(outsideRecord, new Record(insideLength, insideSchema));
                outsideRecord = outsideSource.next();
                insideSource.reset();
                return result;
            }
            else if (outsideSource.hasNext()) {
                outsideRecord = outsideSource.next();
                insideSource.reset();
            }
            else if (!outsideRecord.used()) {
                terminate = true;
                return concat(outsideRecord, new Record(insideLength, insideSchema));
            }
            else {
                return null;
            }
        }
    }

    private Record computeNextFullRecord(int outsideIndex, int insideIndex) {
        if (outsideRecord == null) {
            return null;
        }
        else if (terminate) {
            while (true) {
                if (insideSource.hasNext()) {
                    Record insideRecord = insideSource.next();
                    if (!insideRecord.used()) {
                        return concat(new Record(outsideLength, outsideSchema), insideRecord);
                    }
                }
                else {
                    return null;
                }
            }
        }
        while (true) {
            if (insideSource.hasNext()) {
                Record insideRecord = insideSource.next();
                if (insideRecord.getData().get(insideIndex).getEvalExpression().equals(outsideRecord.getData().get(outsideIndex).getEvalExpression())) {
                    outsideRecord.markUsed();
                    insideRecord.markUsed();
                    return concat(outsideRecord, insideRecord);
                }
            }
            else if (outsideSource.hasNext() && !outsideRecord.used()) {
                Record result = concat(outsideRecord, new Record(insideLength, insideSchema));
                outsideRecord = outsideSource.next();
                insideSource.reset();
                return result;
            }
            else if (outsideSource.hasNext()) {
                outsideRecord = outsideSource.next();
                insideSource.reset();
            }
            else if (!outsideRecord.used()) {
                terminate = true;
                insideSource.reset();
                return concat(outsideRecord, new Record(insideLength, insideSchema));
            }
            else {
                return null;
            }
        }
    }

    private Record concat(Record outsideRecord, Record insideRecord) {
        if (removeDuplicate) {
            return outsideRecord.concatExcept(insideRecord, insideIndex);
        }
        else {
            return outsideRecord.concat(insideRecord);
        }
    }

    @Override
    public void markPrev() {
        outsideSource.markPrev();
        insideSource.markPrev();
    }

    @Override
    public void markNext() {
        outsideSource.markNext();
        insideSource.markNext();
    }

    @Override
    public void reset() {
        outsideSource.reset();
        insideSource.reset();
    }

    @Override
    public void markStart() {
        outsideSource.markStart();
        insideSource.markStart();
    }

}