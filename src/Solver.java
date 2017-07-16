import model.Constraint;
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

        Set<E> savedState = new LinkedHashSet<>(unassigned.getCurrentDomain());
        for (E o : orderDomainValues(unassigned)) {
            if (unassigned.assign(o)) {
                Solution solution = backtrack(selectUnassignedVariable());
                if (solution != null)
                    return solution;
            }
            unassigned.setCurrentDomain(savedState);
        }
        return null;
    }

    private <E> List<E> orderDomainValues(Variable<E> variable) {
        return new ArrayList<>(variable.getCurrentDomain());
    }

    private Variable<?> selectUnassignedVariable() {
        List<Variable> unassigned = variables.stream().filter(v -> !v.isAssigned()).collect(Collectors.toList());
        return unassigned.size() == 0 ? null : unassigned.get(0);
    }

    public Iterator<Solution> solutionsIterator() {
        return new SolutionsIterator();
    }

    private class SolutionsIterator implements Iterator<Solution> {
        private Solution nextSolution;
        private boolean noMoreSolutions;
        private final Deque<List> domainsStack;
        private final Deque<Variable> variablesStack;

        private void lazyFindNextSolution() {
            while (!variablesStack.isEmpty()) {
                Variable v = variablesStack.peek();
                List domain = domainsStack.peek();
                Iterator domainIterator = domain.iterator();

                while (domainIterator.hasNext()) {
                    Object o = domainIterator.next();
                    v.assign(o);
                    domainIterator.remove();
                    if (variablesStack.size() == variables.size()) {
                        boolean isASolution = constraints.stream().allMatch(Constraint::satisfied) && variables.stream().allMatch(Variable::isAssigned);
                        if (isASolution) {
                            this.nextSolution = new Solution(variables);
                            return;
                        }
                    } else {
                        variablesStack.push(variables.get(variablesStack.size()));
                        domainsStack.push(new LinkedList<>(variablesStack.peek().getDomain()));
                        break;
                    }
                }
                // backtrack if needed
                while (!domainsStack.isEmpty() && domainsStack.peek().isEmpty()) {
                    variablesStack.pop();
                    domainsStack.pop();
                }
            }
            this.noMoreSolutions = true;
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
            this.variablesStack = new LinkedList<>();
            this.domainsStack = new LinkedList<>();
            variablesStack.push(variables.get(variablesStack.size()));
            domainsStack.push(new LinkedList<>(variablesStack.peek().getDomain()));
        }
    }
}
