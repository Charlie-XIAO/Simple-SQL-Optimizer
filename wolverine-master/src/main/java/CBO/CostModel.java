package CBO;

public class CostModel {

    private final double CPU_WEIGHT = 0.3;
 
    private double cost;
    private double cpuCost;
    private double memoryCost;

    public CostModel() {}

    public CostModel(double cpuCost, double memoryCost) {
        this.cpuCost = cpuCost;
        this.memoryCost = memoryCost;
        this.cost = cpuCost * CPU_WEIGHT + memoryCost * (1 - CPU_WEIGHT);
    }

    public double getCpuCost() {
        return cpuCost;
    }

    public double getMemoryCost() {
        return memoryCost;
    }

    public double getCost() {
        return cost;
    }

    public void setCpuCost(double cpuCost) {
        this.cpuCost = cpuCost;
        this.cost = cpuCost * CPU_WEIGHT + memoryCost * (1 - CPU_WEIGHT);
    }

    public void setMemoryCost(double memoryCost) {
        this.memoryCost = memoryCost;
        this.cost = cpuCost * CPU_WEIGHT + memoryCost * (1 - CPU_WEIGHT);
    }

    public void setCost(double cpuCost, double memoryCost) {
        this.memoryCost = memoryCost;
        this.cpuCost = cpuCost;
        this.cost = cpuCost * CPU_WEIGHT + memoryCost * (1 - CPU_WEIGHT);
    }

}