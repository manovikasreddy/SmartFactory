package np;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represents a boolean formula in Conjunctive Normal Form (CNF):
 * C_1 AND C_2 AND ... AND C_m
 * Used to model factory operational constraints, mutual exclusions, and resource rules.
 */
public class SATFormula {

    private final List<SATClause> clauses;
    private final Map<Integer, String> variableNames;

    public SATFormula() {
        this.clauses = new ArrayList<>();
        this.variableNames = new HashMap<>();
    }

    public void addClause(SATClause clause) {
        if (clause != null && clause.size() > 0) {
            clauses.add(clause);
        }
    }

    public void addClause(int... lits) {
        clauses.add(new SATClause(lits));
    }

    public List<SATClause> getClauses() {
        return Collections.unmodifiableList(clauses);
    }

    public void setVariableName(int var, String name) {
        variableNames.put(var, name);
    }

    public String getVariableName(int var) {
        return variableNames.getOrDefault(var, "x" + var);
    }

    public Map<Integer, String> getVariableNames() {
        return Collections.unmodifiableMap(variableNames);
    }

    public Set<Integer> getVariables() {
        Set<Integer> vars = new HashSet<>();
        for (SATClause c : clauses) {
            for (int lit : c.getLiterals()) {
                vars.add(Math.abs(lit));
            }
        }
        return vars;
    }

    public boolean is3SAT() {
        for (SATClause c : clauses) {
            if (c.size() != 3) {
                return false;
            }
        }
        return true;
    }

    /**
     * Builds a standard set of 3-SAT production constraints representing an industrial shop floor:
     * Variables:
     *  1 = CNC_Alpha_Active
     *  2 = CNC_Beta_Active
     *  3 = Robot_Delta_Active
     *  4 = Lathe_Gamma_Active
     *  5 = Line_Assembly_Active
     *  6 = Power_Saver_Mode
     *
     * Constraints (each clause must evaluate to TRUE):
     *  C1: (CNC_Alpha OR CNC_Beta OR Robot_Delta) -> at least one primary machining center runs
     *  C2: (NOT CNC_Alpha OR NOT CNC_Beta OR Power_Saver_Mode) -> cannot run both CNCs without power mode
     *  C3: (Lathe_Gamma OR Line_Assembly OR CNC_Alpha) -> production throughput requirement
     *  C4: (NOT Robot_Delta OR Line_Assembly OR Power_Saver_Mode) -> robotic cell dependency
     *  C5: (NOT Line_Assembly OR CNC_Beta OR Lathe_Gamma) -> assembly feed dependency
     */
    public static SATFormula createIndustrialProduction3SAT() {
        SATFormula formula = new SATFormula();

        formula.setVariableName(1, "CNC_Alpha_Running");
        formula.setVariableName(2, "CNC_Beta_Running");
        formula.setVariableName(3, "Robot_Delta_Running");
        formula.setVariableName(4, "Lathe_Gamma_Running");
        formula.setVariableName(5, "Assembly_Line_Active");
        formula.setVariableName(6, "Power_Saver_Mode");

        // Clause 1: Production throughput demand (at least one primary machine active)
        formula.addClause(1, 2, 3);

        // Clause 2: Transformer peak power limit (cannot run Alpha and Beta simultaneously under high load)
        formula.addClause(-1, -2, 6);

        // Clause 3: Sub-assembly feeder availability
        formula.addClause(4, 5, 1);

        // Clause 4: Robotic safety coordination rule
        formula.addClause(-3, 5, 6);

        // Clause 5: Continuous assembly supply chain rule
        formula.addClause(-5, 2, 4);

        return formula;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < clauses.size(); i++) {
            sb.append(clauses.get(i).toString(variableNames));
            if (i < clauses.size() - 1) {
                sb.append(" AND\n");
            }
        }
        return sb.toString();
    }
}
