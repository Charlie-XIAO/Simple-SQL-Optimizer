package utils;

import java.util.Iterator;

public interface BackTracingIterator<Record> extends Iterator<Record> {
    void markPrev();

    void markNext();

    void reset();

    void markStart();
}
