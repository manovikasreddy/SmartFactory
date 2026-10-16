package flow;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * Bottleneck Identification using the Max-Flow Min-Cut Theorem.
 * After reaching maximum material flow, the minimum cut partition (S, T) identifies
 * the exact production links operating at 100% capacity that throttle plant throughput.
 */
public class MinCutBottleneckFinder {

    public static class BottleneckReport {
        private final double minCutCapacity;
        private final List<FlowEdge> bottleneckEdges;
        private final List<Integer> sourcePartitionNodes;
        private final List<Integer> sinkPartitionNodes;

        public BottleneckReport(double minCutCapacity, List<FlowEdge> bottleneckEdges,
                                List<Integer> sourcePartitionNodes, List<Integer> sinkPartitionNodes) {
            this.minCutCapacity = minCutCapacity;
            this.bottleneckEdges = bottleneckEdges;
            this.sourcePartitionNodes = sourcePartitionNodes;
            this.sinkPartitionNodes = sinkPartitionNodes;
        }

        public double getMinCutCapacity() {
            return minCutCapacity;
        }

        public List<FlowEdge> getBottleneckEdges() {
            return bottleneckEdges;
        }

        public List<Integer> getSourcePartitionNodes() {
            return sourcePartitionNodes;
        }

        public List<Integer> getSinkPartitionNodes() {
            return sinkPartitionNodes;
        }
    }

    /**
     * Identifies factory bottlenecks by computing the minimum cut on a solved flow network.
     */
    public static BottleneckReport findBottlenecks(FlowNetwork G, int s, int t) {
        // Run BFS on the residual graph starting from source s
        boolean[] reachable = new boolean[G.V()];
        Queue<Integer> queue = new ArrayDeque<>();
        reachable[s] = true;
        queue.add(s);

        while (!queue.isEmpty()) {
            int v = queue.poll();
            for (FlowEdge e : G.adj(v)) {
                int w = e.other(v);
                if (e.residualCapacityTo(w) > 1e-9 && !reachable[w]) {
                    reachable[w] = true;
                    queue.add(w);
                }
            }
        }

        List<Integer> sCut = new ArrayList<>();
        List<Integer> tCut = new ArrayList<>();
        for (int v = 0; v < G.V(); v++) {
            if (reachable[v]) {
                sCut.add(v);
            } else {
                tCut.add(v);
            }
        }

        // Bottleneck edges: edges from reachable (S) to non-reachable (T) with flow == capacity > 0
        List<FlowEdge> bottlenecks = new ArrayList<>();
        double cutCapacity = 0.0;

        for (FlowEdge e : G.edges()) {
            if (reachable[e.from()] && !reachable[e.to()]) {
                bottlenecks.add(e);
                cutCapacity += e.capacity();
            }
        }

        return new BottleneckReport(cutCapacity, bottlenecks, sCut, tCut);
    }
}
