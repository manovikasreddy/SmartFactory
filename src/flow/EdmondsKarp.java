package flow;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * Edmonds-Karp Algorithm for Maximum Flow.
 * Uses Breadth-First Search (BFS) to find shortest augmenting paths in terms of edge count.
 * Guarantees polynomial time complexity of O(V * E^2).
 * Used to determine maximum material throughput through capacity-constrained production links.
 */
public class EdmondsKarp {

    private boolean[] marked;
    private FlowEdge[] edgeTo;
    private double value;
    private int augmentationCount;
    private final List<String> stepLog;

    public EdmondsKarp(FlowNetwork G, int s, int t) {
        validate(s, G.V());
        validate(t, G.V());
        if (s == t) {
            throw new IllegalArgumentException("Source equals sink");
        }

        value = 0.0;
        augmentationCount = 0;
        stepLog = new ArrayList<>();

        while (hasAugmentingPathBFS(G, s, t)) {
            // Find bottleneck capacity along the BFS shortest path
            double bottle = Double.POSITIVE_INFINITY;
            for (int v = t; v != s; v = edgeTo[v].other(v)) {
                bottle = Math.min(bottle, edgeTo[v].residualCapacityTo(v));
            }

            // Augment residual flows
            StringBuilder pathSummary = new StringBuilder();
            List<Integer> path = new ArrayList<>();
            for (int v = t; v != s; v = edgeTo[v].other(v)) {
                edgeTo[v].addResidualFlowTo(v, bottle);
                path.add(0, v);
            }
            path.add(0, s);

            for (int i = 0; i < path.size(); i++) {
                pathSummary.append(G.getNodeLabel(path.get(i)));
                if (i < path.size() - 1) {
                    pathSummary.append(" => ");
                }
            }

            value += bottle;
            augmentationCount++;

            if (augmentationCount <= 12) {
                stepLog.add(String.format("BFS Augmentation #%d (Length: %d hops): Bottleneck = %.2f units | Cumulative = %.2f\n    Route: %s",
                        augmentationCount, path.size() - 1, bottle, value, pathSummary.toString()));
            }
        }
    }

    private boolean hasAugmentingPathBFS(FlowNetwork G, int s, int t) {
        edgeTo = new FlowEdge[G.V()];
        marked = new boolean[G.V()];

        Queue<Integer> queue = new ArrayDeque<>();
        marked[s] = true;
        queue.add(s);

        while (!queue.isEmpty() && !marked[t]) {
            int v = queue.poll();

            for (FlowEdge e : G.adj(v)) {
                int w = e.other(v);

                // If residual capacity is available and w is unvisited
                if (e.residualCapacityTo(w) > 1e-9 && !marked[w]) {
                    edgeTo[w] = e;
                    marked[w] = true;
                    queue.add(w);
                }
            }
        }

        return marked[t];
    }

    public double value() {
        return value;
    }

    public int getAugmentationCount() {
        return augmentationCount;
    }

    public List<String> getStepLog() {
        return stepLog;
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
