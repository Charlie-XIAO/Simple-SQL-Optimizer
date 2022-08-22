package plan;

import java.util.ArrayList;
import java.util.List;

public class HavingNode extends Node {
    private List<HavingItem> items = new ArrayList<>();

    public void addItem(HavingItem item) {
        items.add(item);
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
