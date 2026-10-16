package parallel;

/**
 * Represents a manufacturing production batch / order in the factory.
 * Prioritized for processing on designated machines and production lines.
 */
public class ProductionBatch implements Comparable<ProductionBatch> {

    private final String orderId;
    private final String productName;
    private final int priority;        // 1 = Critical Rush, 10 = Normal, 100 = Low
    private final int quantity;
    private final int deadlineHours;
    private final String targetMachine;

    public ProductionBatch(String orderId, String productName, int priority,
                           int quantity, int deadlineHours, String targetMachine) {
        this.orderId = orderId;
        this.productName = productName;
        this.priority = priority;
        this.quantity = quantity;
        this.deadlineHours = deadlineHours;
        this.targetMachine = targetMachine;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getProductName() {
        return productName;
    }

    public int getPriority() {
        return priority;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getDeadlineHours() {
        return deadlineHours;
    }

    public String getTargetMachine() {
        return targetMachine;
    }

    @Override
    public int compareTo(ProductionBatch other) {
        // Primary sort: priority ascending (1 is highest priority)
        if (this.priority != other.priority) {
            return Integer.compare(this.priority, other.priority);
        }
        // Secondary sort: earliest deadline first
        return Integer.compare(this.deadlineHours, other.deadlineHours);
    }

    @Override
    public String toString() {
        return String.format("[%s] %-22s | Priority: %2d | Qty: %4d units | Due: %2d hrs | Target: %s",
                orderId, productName, priority, quantity, deadlineHours, targetMachine);
    }
}
