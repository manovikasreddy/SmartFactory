package np;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 2-Approximation Algorithm for Vertex Cover.
 * Uses Maximal Matching to find a small set of machines or connections
 * covering all production conflicts.
 *
 * Theoretical Guarantee:
 *  Let M be the maximal matching found.
 *  The algorithm adds both endpoints of every edge in M, so |C| = 2 * |M|.
 *  Since edges in M are mutually disjoint, any valid vertex cover C* must contain
 *  at least one endpoint from each edge in M: |C*| >= |M|.
 *  Therefore: |C| = 2 * |M| <= 2 * |C*|  (2-approximation).
 */
public class VertexCoverApproximation {

    public static class ApproxResult {
        private final List<Integer> vertexCoverNodes;
        private final List<int[]> matchingEdges;
        private final double approximationRatioBound;

        public ApproxResult(List<Integer> vertexCoverNodes, List<int[]> matchingEdges) {
            this.vertexCoverNodes = vertexCoverNodes;
            this.matchingEdges = matchingEdges;
            this.approximationRatioBound = 2.0;
        }

        public List<Integer> getVertexCoverNodes() {
            return vertexCoverNodes;
        }

        public List<int[]> getMatchingEdges() {
            return matchingEdges;
        }

        public double getApproximationRatioBound() {
            return approximationRatioBound;
        }
    }

    /**
     * Computes the 2-approximate vertex cover on the given conflict graph.
     */
    public static ApproxResult compute2Approximation(ConflictGraphReduction.ConflictGraph graph) {
        int n = graph.size();
        boolean[][] edges = new boolean[n][n];

        // Copy edge matrix
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                edges[i][j] = graph.getConflictMatrix()[i][j];
            }
        }

        Set<Integer> cover = new HashSet<>();
        List<int[]> matching = new ArrayList<>();

        for (int u = 0; u < n; u++) {
            for (int v = u + 1; v < n; v++) {
                // If this edge is still uncovered
                if (edges[u][v]) {
                    // Add edge to maximal matching
                    matching.add(new int[]{u, v});
                    cover.add(u);
                    cover.add(v);

                    // Remove all incident edges to u and v
                    for (int k = 0; k < n; k++) {
                        edges[u][k] = false;
                        edges[k][u] = false;
                        edges[v][k] = false;
                        edges[k][v] = false;
                    }
                }
            }
        }

        List<Integer> resultList = new ArrayList<>(cover);
        resultList.sort(Integer::compareTo);

        return new ApproxResult(resultList, matching);
    }
}
