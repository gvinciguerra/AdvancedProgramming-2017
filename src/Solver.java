import model.Constraint;
import model.InconsistencyException;
import model.Variable;

import java.util.*;
import java.util.stream.Collectors;

public class Solver {
    private final List<Variable<?>> variables;
    private final List<Constraint> constraints;

    public Solver(List<? extends Variable<?>> variables, List<Constraint> constraints) {
        this.variables = new ArrayList<>(variables);
        this.constraints = new ArrayList<>(constraints);
    }

    public Solution backtrackingSearch() {
        return backtrack(selectUnassignedVariable());
    }

    private <E> Solution backtrack(Variable<E> unassigned) {
        if (unassigned == null)
            return constraints.stream().allMatch(Constraint::satisfied) ? new Solution(variables) : null;
        if (!constraints.stream().filter(c -> c.getVariables().stream().allMatch(Variable::isAssigned)).allMatch(Constraint::satisfied))
            return null; // partial assignment inconsistency optimization

        for (E o : unassigned.getCurrentDomain()) {
            if (unassigned.assign(o)) {
                Solution solution = backtrack(selectUnassignedVariable());
                if (solution != null)
                    return solution;
            }
            unassigned.unassign();
        }
        return null;
    }

    private Variable<?> selectUnassignedVariable() {
        return variables.stream().filter(v -> !v.isAssigned()).findFirst().orElse(null);
    }

    public Iterator<Solution> solutionsIterator() {
        return new SolutionsIterator();
    }

    private class SolutionsIterator implements Iterator<Solution> {
        private Solution nextSolution;
        private boolean noMoreSolutions;
        private final Deque<Object> assignments = new LinkedList<>();
        private final Deque<List<Constraint>> explanationStack = new LinkedList<>();
        private final Deque<Map<Variable, Set>> propagationsStack = new LinkedList<>();
        private final Map<Variable, List<Constraint>> triggeringConstraints = new HashMap<>();

        private void lazyFindNextSolution() {
            while (true) {
                assert propagationsStack.size() <= variables.size();
                assert propagationsStack.size() == assignments.size();
                assert assignments.size() == variables.stream().filter(Variable::isAssigned).count();

                while (variables.stream().anyMatch(v -> v.getCurrentDomain().isEmpty())) { // Inconsistency. Backtrack
                    if (propagationsStack.isEmpty()) {
                        noMoreSolutions = true;
                        return;
                    }
                    undoLastAssignment();
                }

                boolean isAssignmentComplete = variables.stream().allMatch(Variable::isAssigned);
                if (!isAssignmentComplete) {
                    Variable unassigned = selectUnassignedVariable();
                    assert unassigned.getCurrentDomain().size() > 0;
                    Object o = unassigned.getCurrentDomain().iterator().next();
                    tryAssignAndPropagate(unassigned, o);
                    continue;
                }

                boolean isASolution = constraints.stream().allMatch(Constraint::satisfied);
                if (isASolution) {
                    List<Constraint> flattenExplanation = explanationStack.stream().flatMap(List::stream).collect(Collectors.toList());
                    nextSolution = new Solution(variables, flattenExplanation);
                    undoLastAssignment();
                    return;
                }
            }
        }

        private void undoLastAssignment() {
            explanationStack.pop();
            propagationsStack.pop().forEach(Variable::setCurrentDomain);
            Variable toUndo = variables.get(assignments.size()-1);
            Object o = assignments.pop();
            toUndo.unassign();
            toUndo.getCurrentDomain().remove(o);
        }

        private <E> void tryAssignAndPropagate(Variable<E> unassigned, E value) {
            if (!unassigned.assign(value))
                return;

            Map<Variable, Set> state = new HashMap<>();
            variables.stream().filter(v -> !v.isAssigned()).forEach(v -> state.put(v, new LinkedHashSet<>(v.getCurrentDomain())));
            List<Constraint> explanationStep = new LinkedList<>();
            for (Constraint c : triggeringConstraints.getOrDefault(unassigned, Collections.emptyList()))
                try {
                    boolean propagationDidChangeDomains = false;
                    for (Map.Entry<Variable, Set> savedState : c.propagate().entrySet()) {
                        if (savedState.getKey().getCurrentDomain().size() != savedState.getValue().size())
                            propagationDidChangeDomains = true;
                        if (null == state.putIfAbsent(savedState.getKey(), savedState.getValue()))
                            state.get(savedState.getKey()).addAll(savedState.getValue());
                    }
                    if (propagationDidChangeDomains)
                        explanationStep.add(c);
                } catch (InconsistencyException e) {
                    state.forEach(Variable::setCurrentDomain);
                    unassigned.getCurrentDomain().remove(value);
                    unassigned.unassign();
                    return;
                }
            assignments.push(value);
            propagationsStack.push(state);
            explanationStack.push(explanationStep);
        }

        @Override
        public Solution next() {
            if (noMoreSolutions)
                throw new NoSuchElementException();
            if (nextSolution == null)
                lazyFindNextSolution();
            Solution temp = nextSolution;
            nextSolution = null;
            return temp;
        }

        @Override
        public boolean hasNext() {
            if (!noMoreSolutions && nextSolution == null)
                lazyFindNextSolution();
            return !noMoreSolutions;
        }

        private SolutionsIterator() {
            for (Constraint constraint : constraints)
                for (Variable variable : constraint.getTriggerVariables()) {
                    triggeringConstraints.putIfAbsent(variable, new LinkedList<>());
                    triggeringConstraints.get(variable).add(constraint);
                }
        }
    }
}
