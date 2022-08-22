package utils;

public interface BacktracingIterable<Record> extends Iterable<Record> {
    public BackTracingIterator<Record> backTracingIterator();
}
