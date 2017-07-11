import model.Constraint;
import model.Variable;

import java.util.*;

public class Solver {
    private final List<Variable> variables;
    private final List<Constraint> constraints;
    private Queue<Variable> unassignedVariables;

    public Solver(List<Variable> variables, List<Constraint> constraints) {
        this.variables = new ArrayList<>(variables);
        this.constraints = new ArrayList<>(constraints);
    }

    public List<Variable> backtrackingSearch() {
        this.unassignedVariables = new LinkedList<>(variables);
        return backtrack(selectUnassignedVariable());
    }

    private <E> List<Variable> backtrack(Variable<E> unassigned) {
        if (unassigned == null)
            return constraints.stream().allMatch(Constraint::satisfied) ? this.variables : null;

        this.unassignedVariables.remove(unassigned);
        Set<E> savedState = new LinkedHashSet<>(unassigned.getCurrentDomain());
        for (E o : orderDomainValues(unassigned)) {
            if (unassigned.assign(o)) {
                List<Variable> result = backtrack(selectUnassignedVariable());
                if (result != null)
                    return result;
            }
            unassigned.setCurrentDomain(savedState);
        }
        this.unassignedVariables.add(unassigned);
        return null;
    }

    private <E> List<E> orderDomainValues(Variable<E> variable) {
        return new ArrayList<>(variable.getCurrentDomain());
    }

    private Variable<?> selectUnassignedVariable() {
        assert this.unassignedVariables.isEmpty() || !unassignedVariables.peek().isAssigned();
        return this.unassignedVariables.isEmpty() ? null : unassignedVariables.poll();
    }
}
