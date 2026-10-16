package flow;

import java.util.ArrayList;
import java.util.List;

/**
 * Ford-Fulkerson Method (DFS-based augmenting paths).
 * Models raw material flow through production stages.
 * Demonstrates basic flow augmentation and residual graph updates.
 */
public class FordFulkerson {

    private boolean[] marked;     // marked[v] = true iff s->v path in residual graph
    private FlowEdge[] edgeTo;    // edgeTo[v] = last edge on s->v path
    private double value;         // current value of max flow
    private int augmentationCount;
    private final List<String> augmentationLog;

    public FordFulkerson(FlowNetwork G, int s, int t) {
        validate(s, G.V());
        validate(t, G.V());
        if (s == t) {
            throw new IllegalArgumentException("Source equals sink");
        }

        value = 0.0;
        augmentationCount = 0;
        augmentationLog = new ArrayList<>();

        while (hasAugmentingPathDFS(G, s, t)) {
            // Compute bottleneck capacity along the augmenting path
            double bottle = Double.POSITIVE_INFINITY;
            for (int v = t; v != s; v = edgeTo[v].other(v)) {
                bottle = Math.min(bottle, edgeTo[v].residualCapacityTo(v));
            }

            // Augment flow along path
            StringBuilder pathStr = new StringBuilder();
            List<Integer> pathNodes = new ArrayList<>();
            for (int v = t; v != s; v = edgeTo[v].other(v)) {
                edgeTo[v].addResidualFlowTo(v, bottle);
                pathNodes.add(0, v);
            }
            pathNodes.add(0, s);

            for (int i = 0; i < pathNodes.size(); i++) {
                pathStr.append(G.getNodeLabel(pathNodes.get(i)));
                if (i < pathNodes.size() - 1) {
                    pathStr.append(" -> ");
                }
            }

            value += bottle;
            augmentationCount++;

            if (augmentationCount <= 10) {
                augmentationLog.add(String.format("Path #%d: [%s] | Added Flow: %.2f units",
                        augmentationCount, pathStr.toString(), bottle));
            }
        }
    }

    private boolean hasAugmentingPathDFS(FlowNetwork G, int s, int t) {
        edgeTo = new FlowEdge[G.V()];
        marked = new boolean[G.V()];

        dfs(G, s, t);
        return marked[t];
    }

    private void dfs(FlowNetwork G, int v, int t) {
        marked[v] = true;
        if (v == t) {
            return;
        }

        for (FlowEdge e : G.adj(v)) {
            int w = e.other(v);
            // Residual capacity must be strictly positive
            if (e.residualCapacityTo(w) > 1e-9 && !marked[w]) {
                edgeTo[w] = e;
                dfs(G, w, t);
                if (marked[t]) {
                    return; // path found, early exit
                }
            }
        }
    }

    public double value() {
        return value;
    }

    public int getAugmentationCount() {
        return augmentationCount;
    }

    public List<String> getAugmentationLog() {
        return augmentationLog;
    }

    public boolean inCut(int v) {
        validate(v, marked.length);
        return marked[v];
    }

    private void validate(int v, int n) {
        if (v < 0 || v >= n) {
            throw new IllegalArgumentException("Vertex " + v + " is out of bounds");
        }
    }
}
