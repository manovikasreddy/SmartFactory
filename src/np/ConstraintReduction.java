package np;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 3-SAT to CLIQUE Polynomial-Time Reduction.
 * Demonstrates Karp's classic reduction showing NP-completeness.
 * Given a 3-SAT formula with m clauses:
 *  1. Create a graph G with 3m vertices (each literal occurrence in each clause).
 *  2. Connect two vertices (i, j) and (i', k) iff:
 *     - They belong to different clauses (i != i')
 *     - Their literals are compatible (l_{i,j} != NOT l_{i',k})
 *  3. Theorem: Formula is satisfiable <=> Graph G has a k-clique where k = m.
 */
public class ConstraintReduction {

    public static class ReductionVertex {
        private final int id;
        private final int clauseIndex;
        private final int literal;
        private final String label;

        public ReductionVertex(int id, int clauseIndex, int literal, String label) {
            this.id = id;
            this.clauseIndex = clauseIndex;
            this.literal = literal;
            this.label = label;
        }

        public int getId() {
            return id;
        }

        public int getClauseIndex() {
            return clauseIndex;
        }

        public int getLiteral() {
            return literal;
        }

        public String getLabel() {
            return label;
        }

        @Override
        public String toString() {
            return String.format("V%d[C%d:%s]", id, clauseIndex + 1, label);
        }
    }

    public static class ReductionGraph {
        private final int numClauses;
        private final List<ReductionVertex> vertices;
        private final boolean[][] adjMatrix;
        private final int numEdges;

        public ReductionGraph(int numClauses, List<ReductionVertex> vertices, boolean[][] adjMatrix, int numEdges) {
            this.numClauses = numClauses;
            this.vertices = vertices;
            this.adjMatrix = adjMatrix;
            this.numEdges = numEdges;
        }

        public int getNumClauses() {
            return numClauses;
        }

        public List<ReductionVertex> getVertices() {
            return vertices;
        }

        public boolean[][] getAdjMatrix() {
            return adjMatrix;
        }

        public int getNumEdges() {
            return numEdges;
        }

        public boolean hasEdge(int u, int v) {
            return adjMatrix[u][v];
        }
    }

    public static class CliqueResult {
        private final boolean found;
        private final List<ReductionVertex> cliqueVertices;
        private final Map<Integer, Boolean> derivedAssignment;

        public CliqueResult(boolean found, List<ReductionVertex> cliqueVertices, Map<Integer, Boolean> derivedAssignment) {
            this.found = found;
            this.cliqueVertices = cliqueVertices;
            this.derivedAssignment = derivedAssignment;
        }

        public boolean isFound() {
            return found;
        }

        public List<ReductionVertex> getCliqueVertices() {
            return cliqueVertices;
        }

        public Map<Integer, Boolean> getDerivedAssignment() {
            return derivedAssignment;
        }
    }

    /**
     * Converts a 3-SAT formula into a CLIQUE graph.
     */
    public static ReductionGraph reduceToClique(SATFormula formula) {
        List<SATClause> clauses = formula.getClauses();
        int m = clauses.size();
        List<ReductionVertex> vertices = new ArrayList<>();

        int vid = 0;
        for (int c = 0; c < m; c++) {
            SATClause clause = clauses.get(c);
            for (int lit : clause.getLiterals()) {
                int var = Math.abs(lit);
                String name = formula.getVariableName(var);
                String litLabel = (lit > 0) ? name : "NOT " + name;
                vertices.add(new ReductionVertex(vid++, c, lit, litLabel));
            }
        }

        int totalV = vertices.size();
        boolean[][] adj = new boolean[totalV][totalV];
        int edgeCount = 0;

        for (int i = 0; i < totalV; i++) {
            for (int j = i + 1; j < totalV; j++) {
                ReductionVertex vi = vertices.get(i);
                ReductionVertex vj = vertices.get(j);

                // Different clauses and non-contradictory literals
                if (vi.getClauseIndex() != vj.getClauseIndex() && vi.getLiteral() != -vj.getLiteral()) {
                    adj[i][j] = true;
                    adj[j][i] = true;
                    edgeCount++;
                }
            }
        }

        return new ReductionGraph(m, vertices, adj, edgeCount);
    }

    /**
     * Finds a clique of size k = m in the reduction graph.
     */
    public static CliqueResult findKClique(ReductionGraph graph, int k) {
        List<Integer> currentClique = new ArrayList<>();
        List<Integer> found = new ArrayList<>();

        searchClique(0, currentClique, k, graph, found);

        if (!found.isEmpty()) {
            List<ReductionVertex> cliqueVerts = new ArrayList<>();
            Map<Integer, Boolean> assignment = new HashMap<>();

            for (int idx : found) {
                ReductionVertex v = graph.getVertices().get(idx);
                cliqueVerts.add(v);
                int var = Math.abs(v.getLiteral());
                boolean val = v.getLiteral() > 0;
                assignment.put(var, val);
            }

            return new CliqueResult(true, cliqueVerts, assignment);
        }

        return new CliqueResult(false, Collections.emptyList(), Collections.emptyMap());
    }

    private static boolean searchClique(int startIdx, List<Integer> current, int k, ReductionGraph graph, List<Integer> result) {
        if (current.size() == k) {
            result.addAll(current);
            return true;
        }

        int n = graph.getVertices().size();
        for (int i = startIdx; i < n; i++) {
            // Prune if not enough vertices left
            if (current.size() + (n - i) < k) {
                return false;
            }

            // Check if vertex i connects to all vertices currently in the clique
            boolean canAdd = true;
            for (int inC : current) {
                if (!graph.hasEdge(i, inC)) {
                    canAdd = false;
                    break;
                }
            }

            if (canAdd) {
                current.add(i);
                if (searchClique(i + 1, current, k, graph, result)) {
                    return true;
                }
                current.remove(current.size() - 1);
            }
        }

        return false;
    }
}
