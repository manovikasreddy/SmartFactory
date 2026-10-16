package flow;

/**
 * Represents a directed, capacitated edge in a factory network flow graph.
 * Supports residual capacity queries and flow augmentations.
 */
public class FlowEdge {

    private final int from;
    private final int to;
    private final double capacity;
    private double flow;
    private final String description;

    public FlowEdge(int from, int to, double capacity) {
        this(from, to, capacity, "");
    }

    public FlowEdge(int from, int to, double capacity, String description) {
        if (capacity < 0) {
            throw new IllegalArgumentException("Edge capacity must be non-negative");
        }
        this.from = from;
        this.to = to;
        this.capacity = capacity;
        this.flow = 0.0;
        this.description = description;
    }

    public int from() {
        return from;
    }

    public int to() {
        return to;
    }

    public double capacity() {
        return capacity;
    }

    public double flow() {
        return flow;
    }

    public String getDescription() {
        return description;
    }

    public int other(int vertex) {
        if (vertex == from) {
            return to;
        } else if (vertex == to) {
            return from;
        } else {
            throw new IllegalArgumentException("Invalid endpoint vertex: " + vertex);
        }
    }

    public double residualCapacityTo(int vertex) {
        if (vertex == to) {
            return capacity - flow; // forward edge
        } else if (vertex == from) {
            return flow;            // backward edge
        } else {
            throw new IllegalArgumentException("Invalid endpoint vertex: " + vertex);
        }
    }

    public void addResidualFlowTo(int vertex, double delta) {
        if (vertex == to) {
            flow += delta;          // forward edge
        } else if (vertex == from) {
            flow -= delta;          // backward edge
        } else {
            throw new IllegalArgumentException("Invalid endpoint vertex: " + vertex);
        }
    }

    public void resetFlow() {
        this.flow = 0.0;
    }

    @Override
    public String toString() {
        return String.format("%d -> %d [%.1f/%.1f] %s", from, to, flow, capacity,
                (description != null && !description.isEmpty()) ? "(" + description + ")" : "");
    }
}
