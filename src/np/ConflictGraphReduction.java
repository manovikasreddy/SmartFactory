package np;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Reduction Pipeline: CLIQUE -> INDEPENDENT-SET -> VERTEX-COVER
 * Represents conflicting production activities in smart manufacturing.
 *
 * Concepts:
 * 1. Compatibility Graph G: Edges represent compatible activities that can run together.
 * 2. Conflict Graph G_comp: Complement of G. Edges represent physical or resource conflicts.
 * 3. Theorem 1 (CLIQUE -> INDEPENDENT-SET):
 *    A subset of activities S is a CLIQUE in G <=> S is an INDEPENDENT SET in G_comp.
 *    (Represents the maximum number of mutually compatible production tasks).
 * 4. Theorem 2 (INDEPENDENT-SET -> VERTEX-COVER):
 *    In any graph H = (V, E), S is an INDEPENDENT SET <=> V \ S is a VERTEX COVER.
 *    (Represents the minimum set of machines to inspect or pause to eliminate all conflicts).
 */
public class ConflictGraphReduction {

    public static class ConflictGraph {
        private final List<String> activities;
        private final boolean[][] conflictMatrix;
        private final int numEdges;

        public ConflictGraph(List<String> activities, boolean[][] conflictMatrix) {
            this.activities = activities;
            this.conflictMatrix = conflictMatrix;
            int edges = 0;
            for (int i = 0; i < activities.size(); i++) {
                for (int j = i + 1; j < activities.size(); j++) {
                    if (conflictMatrix[i][j]) {
                        edges++;
                    }
                }
            }
            this.numEdges = edges;
        }

        public List<String> getActivities() {
            return activities;
        }

        public boolean[][] getConflictMatrix() {
            return conflictMatrix;
        }

        public int getNumEdges() {
            return numEdges;
        }

        public int size() {
            return activities.size();
        }

        public boolean hasConflict(int u, int v) {
            return conflictMatrix[u][v];
        }
    }

    /**
     * Builds a factory production conflict graph based on shared industrial resources:
     * Activity 0: Heavy Milling Ingot A (High Amperage, Tool 1)
     * Activity 1: CNC Precision Turning B (Tool 1, Gantry Crane)
     * Activity 2: Robotic Laser Welding C (Gantry Crane, Shield Gas)
     * Activity 3: Thermal Induction Hardening D (High Amperage, Cooling Loop)
     * Activity 4: Automated CMM Inspection E (Shield Gas, Clean Zone)
     * Activity 5: Stamping & Pressing F (High Amperage, Tool 2)
     */
    public static ConflictGraph createFactoryConflictGraph() {
        List<String> activities = new ArrayList<>();
        activities.add("Milling-01 [High Power, Tool 1]");
        activities.add("Turning-02 [Tool 1, Gantry Crane]");
        activities.add("Welding-03 [Gantry Crane, Shield Gas]");
        activities.add("Hardening-04 [High Power, Cooling Loop]");
        activities.add("Inspection-05 [Shield Gas, Clean Zone]");
        activities.add("Stamping-06 [High Power, Tool 2]");

        int n = activities.size();
        boolean[][] conflicts = new boolean[n][n];

        // Define conflict edges based on shared resource bottlenecks
        addConflict(conflicts, 0, 1); // Tool 1 conflict
        addConflict(conflicts, 1, 2); // Gantry crane conflict
        addConflict(conflicts, 0, 3); // High amperage power phase conflict
        addConflict(conflicts, 2, 4); // Shield gas pressure drop conflict
        addConflict(conflicts, 0, 5); // Power transformer peak load conflict
        addConflict(conflicts, 3, 5); // High power concurrent surge conflict

        return new ConflictGraph(activities, conflicts);
    }

    private static void addConflict(boolean[][] matrix, int u, int v) {
        matrix[u][v] = true;
        matrix[v][u] = true;
    }

    /**
     * Computes the complement graph (Compatibility Graph) from the Conflict Graph.
     * In G_comp, an edge exists iff there is NO conflict between u and v.
     */
    public static boolean[][] computeCompatibilityGraph(ConflictGraph conflictGraph) {
        int n = conflictGraph.size();
        boolean[][] compat = new boolean[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i != j && !conflictGraph.hasConflict(i, j)) {
                    compat[i][j] = true;
                }
            }
        }
        return compat;
    }

    /**
     * Finds the Maximum Independent Set in the conflict graph by reduction.
     * (Equivalently, Maximum Clique in the compatibility graph).
     */
    public static List<Integer> findMaxIndependentSet(ConflictGraph graph) {
        int n = graph.size();
        List<Integer> bestIS = new ArrayList<>();

        // Test all subsets (for small factory units n <= 20)
        for (int mask = 0; mask < (1 << n); mask++) {
            List<Integer> subset = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                if ((mask & (1 << i)) != 0) {
                    subset.add(i);
                }
            }

            if (isIndependentSet(subset, graph) && subset.size() > bestIS.size()) {
                bestIS = subset;
            }
        }
        return bestIS;
    }

    /**
     * Derives the Minimum Vertex Cover directly from the Maximum Independent Set via:
     * VC = V \ IS
     */
    public static List<Integer> deriveVertexCoverFromIS(ConflictGraph graph, List<Integer> independentSet) {
        List<Integer> vertexCover = new ArrayList<>();
        Set<Integer> isSet = new HashSet<>(independentSet);

        for (int i = 0; i < graph.size(); i++) {
            if (!isSet.contains(i)) {
                vertexCover.add(i);
            }
        }
        return vertexCover;
    }

    private static boolean isIndependentSet(List<Integer> nodes, ConflictGraph graph) {
        for (int i = 0; i < nodes.size(); i++) {
            for (int j = i + 1; j < nodes.size(); j++) {
                if (graph.hasConflict(nodes.get(i), nodes.get(j))) {
                    return false;
                }
            }
        }
        return true;
    }
}
