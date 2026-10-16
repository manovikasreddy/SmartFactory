package np;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Represents a disjunctive clause of literals in a SAT/3-SAT formula.
 * Each literal is represented as an integer:
 *  +v represents variable v is true
 *  -v represents variable v is false (negation)
 */
public class SATClause {

    private final List<Integer> literals;

    public SATClause(int... lits) {
        this.literals = new ArrayList<>();
        for (int l : lits) {
            if (l == 0) {
                throw new IllegalArgumentException("Variable index cannot be 0");
            }
            if (!literals.contains(l)) {
                literals.add(l);
            }
        }
    }

    public SATClause(List<Integer> lits) {
        this.literals = new ArrayList<>();
        for (int l : lits) {
            if (l != 0 && !this.literals.contains(l)) {
                this.literals.add(l);
            }
        }
    }

    public List<Integer> getLiterals() {
        return Collections.unmodifiableList(literals);
    }

    public int size() {
        return literals.size();
    }

    public boolean contains(int literal) {
        return literals.contains(literal);
    }

    public boolean containsVariable(int var) {
        return literals.contains(var) || literals.contains(-var);
    }

    /**
     * Evaluates the clause under a partial or full variable assignment.
     * @return Boolean.TRUE if satisfied, Boolean.FALSE if all literals are false, null if undecided.
     */
    public Boolean evaluate(Map<Integer, Boolean> assignment) {
        boolean hasUnassigned = false;

        for (int lit : literals) {
            int var = Math.abs(lit);
            if (assignment.containsKey(var)) {
                boolean val = assignment.get(var);
                boolean litVal = (lit > 0) ? val : !val;
                if (litVal) {
                    return Boolean.TRUE; // Clause satisfied by at least one literal
                }
            } else {
                hasUnassigned = true;
            }
        }

        return hasUnassigned ? null : Boolean.FALSE;
    }

    public String toString(Map<Integer, String> varNames) {
        StringBuilder sb = new StringBuilder("(");
        for (int i = 0; i < literals.size(); i++) {
            int lit = literals.get(i);
            int var = Math.abs(lit);
            String name = (varNames != null && varNames.containsKey(var)) ? varNames.get(var) : "x" + var;
            if (lit < 0) {
                sb.append("NOT ");
            }
            sb.append(name);
            if (i < literals.size() - 1) {
                sb.append(" OR ");
            }
        }
        sb.append(")");
        return sb.toString();
    }

    @Override
    public String toString() {
        return toString(null);
    }
}
