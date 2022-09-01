package plan;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import utils.TableColumnTuple;

public class HavingNode extends Node {

    private List<HavingItem> items = new ArrayList<>();
    // RBO column pruning required slot
    Set<TableColumnTuple<String, String>> _required = new HashSet<>();

    public void addItem(HavingItem item) {
        items.add(item);
        TableColumnTuple<String, String> newRequired = new TableColumnTuple<String,String>(item.projectItem.tableName, item.projectItem.columnName);
        if (!_required.contains(newRequired)) {
            _required.add(newRequired);
        }
    }

    public Set<TableColumnTuple<String, String>> getRequired() {
        return _required;
    }

    public String toString() {
        return "Having(" + items.toString() + ")";
    }
}

class HavingItem {

    Comparison comparison;
    String comLiteral;
    ProjectItem projectItem;

    public void setComparison(Comparison comparison) {
        this.comparison = comparison;
    }

    public void setComLiteral(String comLiteral) {
        this.comLiteral = comLiteral;
    }

    public void setProjectItem(ProjectItem projectItem) {
        this.projectItem = projectItem;
    }

    public String toString() {
        return "HavingItem(" + comparison + " " + comLiteral + " " + projectItem + ")";
    }
}
