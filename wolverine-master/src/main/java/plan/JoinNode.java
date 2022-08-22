package plan;

import table.Record;
import plan.type.JoinType;
import plan.type.PhysicalJoinType;
import utils.BackTracingIterator;
import utils.ListBacktracingIterator;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class JoinNode extends Node {

    private JoinType joinType;
    private String tableNameLeft;
    private String tableNameRight;
    private String columnNameLeft;
    private String columnNameRight;
    private PhysicalJoinType physicalJoinType = PhysicalJoinType.BLOCK_NESTED_LOOP_JOIN;

    // JoinNode has `left` and `right` instead of `child` in its parent class Node
    private Node left;
    private Node right;

    private Set<String> _contained;

    public void setLeft(Node left) {
        this.left = left;
        left.setParent(this);
    }

    public void setRight(Node right) {
        this.right = right;
        right.setParent(this);
    }

    public Node getLeft() {
        return this.left;
    }

    public Node getRight() {
        return this.right;
    }

    /**
     * make sure to traverse the left-deep tree correctly
     * with only the `getChild()` method
     */

    @Override
    public Node getChild() {
        return this.left;
    }

    @Override
    public boolean isLeaf() {
        return this.left == null;
    }

    @Override
    public boolean isJoinNode() {
        return true;
    }

    public void setJoinType(JoinType joinType) {
        this.joinType = joinType;
    }

    public void setTableNameLeft(String tableNameLeft) {
        this.tableNameLeft = tableNameLeft;
    }

    public void setTableNameRight(String tableNameRight) {
        this.tableNameRight = tableNameRight;
    }

    public void setColumnNameLeft(String columnNameLeft) {
        this.columnNameLeft = columnNameLeft;
    }

    public void setColumnNameRight(String columnNameRight) {
        this.columnNameRight = columnNameRight;
    }

    public void setContained(Set<String> _contained) {
        this._contained = _contained;
    }

    public String getTableNameLeft() {
        return tableNameLeft;
    }

    public String getTableNameRight() {
        return tableNameRight;
    }

    public String getColumnNameLeft() {
        return columnNameLeft;
    }
    
    public String getColumnNameRight() {
        return columnNameRight;
    }

    public JoinType getJoinType() {
        return joinType;
    }

    public Set<String> getContained() {
        if (_contained != null) {
            return _contained;
        }
        Set<String> _contained = new HashSet<>();
        if (left instanceof ScanNode) {
            _contained.add(((ScanNode) left).getTableName());
        }
        else if (left instanceof FilterNode) {
            _contained.addAll(((FilterNode) left).getTableNames());
        }
        else if (left instanceof JoinNode) {
            _contained.addAll(((JoinNode) left).getContained());
        }
        if (right instanceof ScanNode) {
            _contained.add(((ScanNode) right).getTableName());
        }
        else if (right instanceof FilterNode) {
            _contained.addAll(((FilterNode) right).getTableNames());
        }
        else if (right instanceof JoinNode) {
            _contained.addAll(((JoinNode) right).getContained());
        }
        this._contained = _contained;
        return _contained;
    }

    public String toString() {
        return "Join(" + joinType + " " + tableNameLeft + "." + columnNameLeft + " " + tableNameRight + "."
            + columnNameRight + ")  <-- " + physicalJoinType.toString().toLowerCase();
    }

    public void setPhysicalJoinType(PhysicalJoinType physicalJoinType) {
        this.physicalJoinType = physicalJoinType;
    }

    @Override
    public Iterator<Record> iterator() {
        return backTracingIterator();
    }

    @Override
    public BackTracingIterator<Record> backTracingIterator() {
        switch (physicalJoinType) {
        case DUMMY_NESTED_LOOP_JOIN:
            return new DummyNestedLoopJoinIterator(true);
        case BLOCK_NESTED_LOOP_JOIN:
            return new BlockNestedLoopJoinIterator(true);
        case HASH_JOIN:
            return new HashJoinIterator();
        case SORT_MERGE_JOIN:
            return new SortMergeJoinIterator();
        default:
            return null;
        }
    }

    private class DummyNestedLoopJoinIterator implements BackTracingIterator<Record> {

        private BackTracingIterator<Record> outsideSource;
        private BackTracingIterator<Record> insideSource;
        private Record outsideRecord;
        private Record nextRecord;

        public DummyNestedLoopJoinIterator(boolean isLeftOutside) {
            BackTracingIterator<Record> leftIterator = JoinNode.this.getLeft().backTracingIterator();
            BackTracingIterator<Record> rightIterator = JoinNode.this.getRight().backTracingIterator();
            outsideSource = (isLeftOutside) ? leftIterator : rightIterator;
            insideSource = (isLeftOutside) ? rightIterator : leftIterator;
            outsideRecord = (outsideSource.hasNext()) ? outsideSource.next() : null;
            insideSource.markStart();
            nextRecord = computeNextRecord();
        }

        @Override
        public boolean hasNext() {
            return nextRecord != null;
        }

        @Override
        public Record next() {
            //lazy evaluation
            if (!hasNext()) {
                throw new java.util.NoSuchElementException();
            }
            Record result = nextRecord;
            nextRecord = computeNextRecord();
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
                } else if (outsideSource.hasNext()) {
                    outsideRecord = outsideSource.next();
                    insideSource.reset();
                } else {
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

    private class BlockNestedLoopJoinIterator implements BackTracingIterator<Record> {
        private BackTracingIterator<Record> outsideSource;
        private BackTracingIterator<Record> insideSource;
        private BackTracingIterator<Record> outsideBuffer;
        private BackTracingIterator<Record> outsideBufferBackup;
        private Record outsideRecord;

        private Record insideRecord;
        private Record nextRecord;
        private int bufferSize = 100;

        public BlockNestedLoopJoinIterator(boolean isLeftOutside) {
            BackTracingIterator<Record> leftIterator = JoinNode.this.getLeft().backTracingIterator();
            BackTracingIterator<Record> rightIterator = JoinNode.this.getRight().backTracingIterator();
            outsideSource = (isLeftOutside) ? leftIterator : rightIterator;
            insideSource = (isLeftOutside) ? rightIterator : leftIterator;

            List<Record> buffer = new java.util.ArrayList<Record>();
            for (int i = 0; i < bufferSize; i++) {
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
                    List<Record> buffer = new java.util.ArrayList<Record>();
                    for (int i = 0; i < bufferSize; i++) {
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

    private class HashJoinIterator implements BackTracingIterator<Record> {
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

    private class SortMergeJoinIterator implements BackTracingIterator<Record> {
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

}