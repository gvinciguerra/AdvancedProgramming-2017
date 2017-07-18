import model.Constraint;
import model.InconsistencyException;
import model.Variable;

import java.util.*;
import java.util.stream.Collectors;

public class Solver {
    private final List<Variable<?>> variables;
    private final List<Constraint> constraints;

    public Solver(List<Variable<?>> variables, List<Constraint> constraints) {
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
        private final Queue<Solution> solutionsQueue = new LinkedList<>();
        private final Deque<Map<Variable, Set>> propagationsStack = new LinkedList<>();

        private void lazyFindNextSolution() {
            while (true) {
                if (!solutionsQueue.isEmpty()) {
                    this.nextSolution = solutionsQueue.poll();
                    return;
                }
                assert propagationsStack.size() <= variables.size();
                assert propagationsStack.size() == assignments.size();

                // Inconsistency. Backtrack
                while (variables.stream().anyMatch(v -> v.getCurrentDomain().isEmpty())) {
                    if (propagationsStack.isEmpty()) {
                        noMoreSolutions = true;
                        return;
                    }
                    undoLastAssignment();
                }

                boolean isAssignmentComplete = variables.stream().allMatch(Variable::isAssigned);
                if (!isAssignmentComplete) {
                    Variable unassigned = selectUnassignedVariable();
                    List<Constraint> constraintsToTrigger = constraints.stream()
                            .filter(c -> c.getTriggerVariables().contains(unassigned)).collect(Collectors.toList());
                    assert unassigned.getCurrentDomain().size() > 0;
                    Object o = unassigned.getCurrentDomain().iterator().next();
                    tryAssignAndPropagate(unassigned, o, constraintsToTrigger);
                    continue;
                }

                boolean isASolution = constraints.stream().allMatch(Constraint::satisfied);
                if (isASolution) {
                    nextSolution = new Solution(variables);
                    undoLastAssignment();
                    return;
                }
            }
        }

        private void undoLastAssignment() {
            propagationsStack.pop().forEach(Variable::setCurrentDomain);
            Variable toUndo = variables.get(assignments.size()-1);
            Object o = assignments.pop();
            toUndo.unassign();
            toUndo.getCurrentDomain().remove(o);
        }

        private void tryAssignAndPropagate(Variable unassigned, Object value, List<Constraint> constraintsToCheck) {
            if (!unassigned.assign(value))
                return;

            Map<Variable, Set> state = new HashMap<>();
            variables.stream().filter(v -> !v.isAssigned()).forEach(w -> state.put(w, new LinkedHashSet<>(w.getCurrentDomain())));
            for (Constraint c : constraintsToCheck)
                try {
                    c.propagate().forEach((v, s) -> { state.putIfAbsent(v, s);  state.get(v).addAll(s); });
                } catch (InconsistencyException e) {
                    state.forEach(Variable::setCurrentDomain);
                    state.clear();
                    unassigned.getCurrentDomain().remove(value);
                    return;
                }
            assignments.push(value);
            propagationsStack.push(state);
        }

        @Override
        public Solution next() {
            if (this.noMoreSolutions)
                throw new NoSuchElementException();
            if (this.nextSolution == null)
                lazyFindNextSolution();
            Solution temp = this.nextSolution;
            this.nextSolution = null;
            return temp;
        }

        @Override
        public boolean hasNext() {
            if (!this.noMoreSolutions && this.nextSolution == null)
                lazyFindNextSolution();
            return !this.noMoreSolutions;
        }

        public SolutionsIterator() {
        }
    }
}
