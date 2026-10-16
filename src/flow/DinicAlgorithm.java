package flow;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;

/**
 * Dinic's Algorithm for Maximum Flow.
 * Combines Level Graphs (via BFS) with Blocking Flows (via DFS and edge pointers).
 * Achieves O(V^2 * E) time complexity, outperforming Edmonds-Karp on dense and large factory networks.
 * Ideal for enterprise multi-plant or multi-tier manufacturing supply chain networks.
 */
public class DinicAlgorithm {

    private final int[] level;
    private final int[] ptr;
    private double maxFlow;
    private int phaseCount;
    private long executionTimeNanos;

    public DinicAlgorithm(FlowNetwork G, int s, int t) {
        level = new int[G.V()];
        ptr = new int[G.V()];
        maxFlow = 0;
        phaseCount = 0;

        long startTime = System.nanoTime();

        // While sink is reachable in the residual level graph
        while (bfs(G, s, t)) {
            Arrays.fill(ptr, 0);
            while (true) {
                double pushed = dfs(G, s, t, Double.POSITIVE_INFINITY);
                if (pushed <= 1e-9) {
                    break;
                }
                maxFlow += pushed;
            }
            phaseCount++;
        }

        executionTimeNanos = System.nanoTime() - startTime;
    }

    private boolean bfs(FlowNetwork G, int s, int t) {
        Arrays.fill(level, -1);
        level[s] = 0;
        Queue<Integer> queue = new ArrayDeque<>();
        queue.add(s);

        while (!queue.isEmpty()) {
            int v = queue.poll();
            for (FlowEdge e : G.adj(v)) {
                int w = e.other(v);
                if (level[w] < 0 && e.residualCapacityTo(w) > 1e-9) {
                    level[w] = level[v] + 1;
                    queue.add(w);
                }
            }
        }
        return level[t] != -1;
    }

    private double dfs(FlowNetwork G, int v, int t, double pushed) {
        if (pushed <= 1e-9) {
            return 0;
        }
        if (v == t) {
            return pushed;
        }

        // Get adjacent edges list
        java.util.List<FlowEdge> adjList = (java.util.List<FlowEdge>) G.adj(v);

        for (int cid = ptr[v]; cid < adjList.size(); cid = ++ptr[v]) {
            FlowEdge e = adjList.get(cid);
            int tr = e.other(v);

            // Forward progress in level graph with residual capacity
            if (level[v] + 1 != level[tr] || e.residualCapacityTo(tr) <= 1e-9) {
                continue;
            }

            double trPushed = dfs(G, tr, t, Math.min(pushed, e.residualCapacityTo(tr)));
            if (trPushed > 1e-9) {
                e.addResidualFlowTo(tr, trPushed);
                return trPushed;
            }
        }
        return 0;
    }

    public double getMaxFlow() {
        return maxFlow;
    }

    public int getPhaseCount() {
        return phaseCount;
    }

    public long getExecutionTimeNanos() {
        return executionTimeNanos;
    }
}
