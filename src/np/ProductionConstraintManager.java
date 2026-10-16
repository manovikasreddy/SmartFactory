package np;

import java.util.List;
import java.util.Map;

/**
 * High-level coordinator for CO-5 NP-Completeness and Approximation.
 * Demonstrates:
 * 1. SAT/3-SAT production constraint formulation & DPLL solving.
 * 2. 3-SAT -> CLIQUE polynomial-time reduction.
 * 3. CLIQUE -> INDEPENDENT-SET -> VERTEX-COVER reduction for conflicting activities.
 * 4. Vertex-Cover 2-approximation for production conflict resolution.
 */
public class ProductionConstraintManager {

    /**
     * Solves industrial 3-SAT production constraints using DPLL.
     */
    public static void solveProductionConstraints() {
        System.out.println("==========================================");
        System.out.println(" PRODUCTION CONSTRAINT SOLVER [3-SAT / DPLL]");
        System.out.println("==========================================");

        SATFormula formula = SATFormula.createIndustrialProduction3SAT();

        System.out.println("Industrial Rule Base (CNF Formulation):");
        System.out.println("Variables:");
        for (Map.Entry<Integer, String> entry : formula.getVariableNames().entrySet()) {
            System.out.printf("  x%d = %s%n", entry.getKey(), entry.getValue());
        }

        System.out.println("\n3-CNF Constraint Clauses:");
        int cIdx = 1;
        for (SATClause clause : formula.getClauses()) {
            System.out.printf("  C%d: %s%n", cIdx++, clause.toString(formula.getVariableNames()));
        }

        System.out.println("\nExecuting DPLL (Unit Propagation + Pure Literal + Backtracking)...");
        SATConstraintSolver solver = new SATConstraintSolver();
        SATConstraintSolver.SolveResult res = solver.solve(formula);

        System.out.println("\nSolver Resolution Steps:");
        for (String traceStep : res.getResolutionTrace()) {
            System.out.println("  * " + traceStep);
        }

        System.out.println("\n------------------------------------------");
        System.out.println("Satisfiable: " + (res.isSatisfiable() ? "YES (Constraints Validated)" : "NO (Infeasible Rules)"));
        System.out.println("Decisions: " + res.getDecisions() + " | Unit Propagations: " + res.getUnitPropagations());

        if (res.isSatisfiable()) {
            System.out.println("\nFeasible Shop Floor Operating Configuration:");
            for (Map.Entry<Integer, Boolean> entry : res.getModel().entrySet()) {
                String varName = formula.getVariableName(entry.getKey());
                System.out.printf("  [STATE] %-26s : %s%n", varName, entry.getValue() ? "ACTIVE (ON)" : "INACTIVE (OFF)");
            }
        }
        System.out.println("==========================================");
    }

    /**
     * Demonstrates 3-SAT -> CLIQUE reduction.
     */
    public static void demonstrate3SatToCliqueReduction() {
        System.out.println("==========================================");
        System.out.println("   CONSTRAINT REDUCTION [3-SAT -> CLIQUE]");
        System.out.println("==========================================");

        SATFormula formula = SATFormula.createIndustrialProduction3SAT();
        int m = formula.getClauses().size();

        System.out.printf("Input Formula: %d 3-SAT clauses, %d variables%n", m, formula.getVariables().size());
        System.out.println("Applying Karp Polynomial Reduction to CLIQUE graph...");

        ConstraintReduction.ReductionGraph rGraph = ConstraintReduction.reduceToClique(formula);

        System.out.println("\nReduction Graph Constructed:");
        System.out.printf("  Total Vertices : %d (3 literals x %d clauses)%n", rGraph.getVertices().size(), m);
        System.out.printf("  Total Edges    : %d non-contradictory cross-clause links%n", rGraph.getNumEdges());
        System.out.printf("  Target k-Clique: k = %d (exactly 1 true literal per clause)%n", m);

        System.out.printf("\nSearching for %d-Clique in reduction graph...%n", m);
        ConstraintReduction.CliqueResult clique = ConstraintReduction.findKClique(rGraph, m);

        if (clique.isFound()) {
            System.out.printf("Found %d-Clique! Vertices:%n", m);
            for (ConstraintReduction.ReductionVertex v : clique.getCliqueVertices()) {
                System.out.printf("  - %s (Literal %+d)%n", v.toString(), v.getLiteral());
            }

            System.out.println("\nSatisfying Assignment extracted from Clique Vertices:");
            for (Map.Entry<Integer, Boolean> entry : clique.getDerivedAssignment().entrySet()) {
                System.out.printf("  %s = %s%n", formula.getVariableName(entry.getKey()), entry.getValue());
            }
            System.out.println("\nTheorem verified: 3-SAT is Satisfiable <=> k-Clique exists.");
        } else {
            System.out.println("No " + m + "-clique found. Formula is unsatisfiable.");
        }
        System.out.println("==========================================");
    }

    /**
     * Demonstrates CLIQUE -> INDEPENDENT-SET -> VERTEX-COVER for conflicting production activities.
     */
    public static void demonstrateConflictActivityReduction() {
        System.out.println("==========================================");
        System.out.println(" CONFLICT REDUCTION [CLIQUE -> IS -> VC]");
        System.out.println("==========================================");

        ConflictGraphReduction.ConflictGraph conflictGraph = ConflictGraphReduction.createFactoryConflictGraph();

        System.out.println("Manufacturing Operations & Shared Resource Conflicts:");
        for (int i = 0; i < conflictGraph.size(); i++) {
            System.out.printf("  [%d] %s%n", i, conflictGraph.getActivities().get(i));
        }

        System.out.println("\nConflict Links (Resource Contention): " + conflictGraph.getNumEdges() + " active edges");
        for (int i = 0; i < conflictGraph.size(); i++) {
            for (int j = i + 1; j < conflictGraph.size(); j++) {
                if (conflictGraph.hasConflict(i, j)) {
                    System.out.printf("  * Conflict: %s <===> %s%n",
                            conflictGraph.getActivities().get(i), conflictGraph.getActivities().get(j));
                }
            }
        }

        System.out.println("\nStep 1: CLIQUE on Compatibility Graph <==> INDEPENDENT SET on Conflict Graph");
        System.out.println("An Independent Set represents mutually non-conflicting tasks that CAN execute simultaneously.");
        List<Integer> maxIS = ConflictGraphReduction.findMaxIndependentSet(conflictGraph);

        System.out.printf("\nMaximum Independent Set (Size = %d compatible operations):%n", maxIS.size());
        for (int idx : maxIS) {
            System.out.println("  [SCHEDULED] " + conflictGraph.getActivities().get(idx));
        }

        System.out.println("\nStep 2: INDEPENDENT SET <==> VERTEX COVER Reduction");
        System.out.println("Theorem: S is an Independent Set <=> V \\ S is a Vertex Cover.");
        System.out.println("A Vertex Cover represents the minimum operations to hold/stagger to eliminate all conflicts.");

        List<Integer> minVC = ConflictGraphReduction.deriveVertexCoverFromIS(conflictGraph, maxIS);
        System.out.printf("\nMinimum Vertex Cover (Size = |V| - |IS| = %d - %d = %d tasks):%n",
                conflictGraph.size(), maxIS.size(), minVC.size());
        for (int idx : minVC) {
            System.out.println("  [HOLD / STAGGER] " + conflictGraph.getActivities().get(idx));
        }

        System.out.println("\nReduction duality validated: Max-IS size + Min-VC size = |V| (" +
                (maxIS.size() + minVC.size()) + " == " + conflictGraph.size() + ").");
        System.out.println("==========================================");
    }

    /**
     * Demonstrates Vertex-Cover 2-Approximation for resolving factory conflicts.
     */
    public static void resolveConflicts2Approximation() {
        System.out.println("==========================================");
        System.out.println(" CONFLICT RESOLUTION [VERTEX-COVER 2-APPROX]");
        System.out.println("==========================================");

        ConflictGraphReduction.ConflictGraph conflictGraph = ConflictGraphReduction.createFactoryConflictGraph();

        System.out.println("Factory Conflict Graph: " + conflictGraph.size() + " tasks, " +
                conflictGraph.getNumEdges() + " conflict edges.");
        System.out.println("Computing 2-Approximation using Maximal Matching Edge Selection...");

        VertexCoverApproximation.ApproxResult approx =
                VertexCoverApproximation.compute2Approximation(conflictGraph);

        System.out.println("\nMaximal Disjoint Edge Matching Chosen (M):");
        for (int[] edge : approx.getMatchingEdges()) {
            System.out.printf("  Edge: [%s] <---> [%s]%n",
                    conflictGraph.getActivities().get(edge[0]),
                    conflictGraph.getActivities().get(edge[1]));
        }

        System.out.printf("\n2-Approximation Vertex Cover Result (Size: %d):%n", approx.getVertexCoverNodes().size());
        for (int node : approx.getVertexCoverNodes()) {
            System.out.printf("  [COVER NODE] Task %d: %s%n", node, conflictGraph.getActivities().get(node));
        }

        System.out.println("\nApproximation Guarantee Analysis:");
        System.out.printf("  Maximal Matching Size |M| : %d%n", approx.getMatchingEdges().size());
        System.out.printf("  Approx Cover Size |C|     : %d  (= 2 * |M|)%n", approx.getVertexCoverNodes().size());
        System.out.println("  Since any valid cover must touch at least 1 endpoint of each disjoint matched edge,");
        System.out.printf("  Optimal |C*| >= %d. Hence: |C| <= 2 * |C*| (Guaranteed within 2x optimal).%n",
                approx.getMatchingEdges().size());
        System.out.println("Vertex Cover 2-approximation completed successfully.");
        System.out.println("==========================================");
    }
}
