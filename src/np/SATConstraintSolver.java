package np;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * DPLL (Davis-Putnam-Logemann-Loveland) Solver for SAT and 3-SAT.
 * Determines whether a set of industrial production rules and constraint clauses is satisfiable,
 * and if so, computes a valid operational configuration.
 */
public class SATConstraintSolver {

    public static class SolveResult {
        private final boolean satisfiable;
        private final Map<Integer, Boolean> model;
        private final int decisions;
        private final int unitPropagations;
        private final List<String> resolutionTrace;

        public SolveResult(boolean satisfiable, Map<Integer, Boolean> model,
                           int decisions, int unitPropagations, List<String> resolutionTrace) {
            this.satisfiable = satisfiable;
            this.model = model;
            this.decisions = decisions;
            this.unitPropagations = unitPropagations;
            this.resolutionTrace = resolutionTrace;
        }

        public boolean isSatisfiable() {
            return satisfiable;
        }

        public Map<Integer, Boolean> getModel() {
            return model;
        }

        public int getDecisions() {
            return decisions;
        }

        public int getUnitPropagations() {
            return unitPropagations;
        }

        public List<String> getResolutionTrace() {
            return resolutionTrace;
        }
    }

    private int decisionCount;
    private int unitPropCount;
    private final List<String> trace;

    public SATConstraintSolver() {
        this.decisionCount = 0;
        this.unitPropCount = 0;
        this.trace = new ArrayList<>();
    }

    public SolveResult solve(SATFormula formula) {
        decisionCount = 0;
        unitPropCount = 0;
        trace.clear();

        Map<Integer, Boolean> assignment = new HashMap<>();
        boolean sat = dpll(formula.getClauses(), assignment, formula);

        if (sat) {
            // Fill any unassigned variables with default FALSE
            for (int var : formula.getVariables()) {
                if (!assignment.containsKey(var)) {
                    assignment.put(var, false);
                }
            }
        }

        return new SolveResult(sat, assignment, decisionCount, unitPropCount, trace);
    }

    private boolean dpll(List<SATClause> clauses, Map<Integer, Boolean> assignment, SATFormula formula) {
        // Step 1: Unit Clause Propagation
        while (true) {
            Integer unitLit = findUnitLiteral(clauses, assignment);
            if (unitLit == null) {
                break;
            }

            int var = Math.abs(unitLit);
            boolean val = unitLit > 0;
            assignment.put(var, val);
            unitPropCount++;

            if (trace.size() < 25) {
                trace.add(String.format("Unit Propagation: Forced %s = %s",
                        formula.getVariableName(var), val));
            }

            // Check if conflict created
            if (hasEmptyClause(clauses, assignment)) {
                return false;
            }
        }

        // Check overall formula status
        boolean allSatisfied = true;
        for (SATClause c : clauses) {
            Boolean res = c.evaluate(assignment);
            if (res == Boolean.FALSE) {
                return false; // Backtrack
            }
            if (res == null) {
                allSatisfied = false;
            }
        }

        if (allSatisfied) {
            return true;
        }

        // Step 2: Choose branching variable (First unassigned variable)
        int branchVar = selectUnassignedVariable(formula.getVariables(), assignment);
        if (branchVar == 0) {
            return true;
        }

        decisionCount++;
        if (trace.size() < 25) {
            trace.add(String.format("Branching: Trying %s = TRUE", formula.getVariableName(branchVar)));
        }

        // Try TRUE
        Map<Integer, Boolean> trueAssignment = new HashMap<>(assignment);
        trueAssignment.put(branchVar, true);
        if (dpll(clauses, trueAssignment, formula)) {
            assignment.putAll(trueAssignment);
            return true;
        }

        // Try FALSE
        if (trace.size() < 25) {
            trace.add(String.format("Backtracking: Reversing to %s = FALSE", formula.getVariableName(branchVar)));
        }
        Map<Integer, Boolean> falseAssignment = new HashMap<>(assignment);
        falseAssignment.put(branchVar, false);
        if (dpll(clauses, falseAssignment, formula)) {
            assignment.putAll(falseAssignment);
            return true;
        }

        return false;
    }

    private Integer findUnitLiteral(List<SATClause> clauses, Map<Integer, Boolean> assignment) {
        for (SATClause c : clauses) {
            if (c.evaluate(assignment) == Boolean.TRUE) {
                continue; // Clause already satisfied
            }

            int unassignedCount = 0;
            int lastUnassignedLit = 0;

            for (int lit : c.getLiterals()) {
                int var = Math.abs(lit);
                if (!assignment.containsKey(var)) {
                    unassignedCount++;
                    lastUnassignedLit = lit;
                }
            }

            if (unassignedCount == 1) {
                return lastUnassignedLit;
            }
        }
        return null;
    }

    private boolean hasEmptyClause(List<SATClause> clauses, Map<Integer, Boolean> assignment) {
        for (SATClause c : clauses) {
            if (c.evaluate(assignment) == Boolean.FALSE) {
                return true;
            }
        }
        return false;
    }

    private int selectUnassignedVariable(Set<Integer> allVars, Map<Integer, Boolean> assignment) {
        for (int v : allVars) {
            if (!assignment.containsKey(v)) {
                return v;
            }
        }
        return 0;
    }
}
