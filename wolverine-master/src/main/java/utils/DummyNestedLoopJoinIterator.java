package utils;

import plan.JoinNode;
import plan.type.JoinType;
import table.Record;
import table.Column;

import java.util.ArrayList;
import java.util.List;

public class DummyNestedLoopJoinIterator implements BackTracingIterator<Record> {

    private BackTracingIterator<Record> outsideSource;
    private BackTracingIterator<Record> insideSource;
    private Record outsideRecord;
    private Record nextRecord;
    private List<Column> joinedSchema;
    
    private JoinType joinType;
    private int outsideIndex, insideIndex;
    private int outsideLength, insideLength;  // outer joins required null record length
    private List<Column> outsideSchema, insideSchema; // outer joins required null record schema
    private boolean terminate = false;  // outer joins required terminate condition

    public DummyNestedLoopJoinIterator(JoinNode joinNode, boolean isLeftOutside) {
        this.joinType = joinNode.getJoinType();
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
            joinedSchema.addAll(tempInsideRecord.getSchema());
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
            joinedSchema.addAll(insideSchema);
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
            joinedSchema.addAll(insideSchema);
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
            joinedSchema.addAll(insideSchema);
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
            joinedSchema.addAll(tempInsideRecord.getSchema());
            nextRecord = computeNextRecord();
        }
        joinNode.setTableSchema(joinedSchema);
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
        return result;
    }

    private Record computeNextRecord() {
        if (outsideRecord == null) {
            return null;
        }
        while (true) {
            if (insideSource.hasNext()) {
                Record insideRecord = insideSource.next();
                return outsideRecord.concat(insideRecord);
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
                    return outsideRecord.concat(insideRecord);
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
                    return outsideRecord.concat(insideRecord);
                }
            }
            else if (outsideSource.hasNext() && !outsideRecord.used()) {
                Record result = outsideRecord.concat(new Record(insideLength, insideSchema));
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
                return outsideRecord.concat(new Record(insideLength, insideSchema));
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
                        return new Record(outsideLength, outsideSchema).concat(insideRecord);
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
                    return outsideRecord.concat(insideRecord);
                }
            }
            else if (outsideSource.hasNext() && !outsideRecord.used()) {
                Record result = outsideRecord.concat(new Record(insideLength, insideSchema));
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
                return outsideRecord.concat(new Record(insideLength, insideSchema));
            }
            else {
                return null;
            }
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