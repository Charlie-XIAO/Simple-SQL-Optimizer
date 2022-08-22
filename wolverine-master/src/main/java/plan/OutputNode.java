package plan;

import table.Record;

import java.util.Iterator;

public class OutputNode extends Node {
    
    public OutputNode() {
        this.height = 0;
    }

    public String toString() {
        return "Output()";
    }

    @Override
    public Iterator<Record> iterator() {
        return this.getChild().iterator();
    }

    public void execute() {
        Iterator<Record> iterator = this.iterator();
        int count = 0;
        while (iterator.hasNext()) {
            Record record = iterator.next();
            System.out.print("[" + count + "] ");
            System.out.println(record);
            count++;
        }
    }
}
