package model;

import java.util.*;
import java.util.function.Predicate;

public class ImplicationConstraint<L, R> implements Constraint {
    private final Predicate<L> premise;
    private final Predicate<R> filter;
    private final Variable<L> lvariable;
    private final Variable<R> rvariable;
    private final Set<Variable> variables;
    private final Set<Variable> triggerVariables;

    public ImplicationConstraint(Variable<L> lvar, Predicate<L> premise, Variable<R> rvar, Predicate<R> filter) {
        this.lvariable = lvar;
        this.rvariable = rvar;
        this.filter = filter;
        this.premise = premise;
        this.variables = Collections.unmodifiableSet(new LinkedHashSet<>(Arrays.asList(lvariable, rvariable)));
        this.triggerVariables = Collections.singleton(lvariable);
    }

    @Override
    public Set<Variable> getTriggerVariables() {
        return triggerVariables;
    }

    @Override
    public Map<Variable, Set> propagate() throws InconsistencyException {
        Map<Variable, Set> domainsCopy = Collections.singletonMap(rvariable, new LinkedHashSet<>(rvariable.getCurrentDomain()));
        if (lvariable.isAssigned()
                && premise.test(lvariable.getValue())
                && rvariable.removeIf(filter)) {
            domainsCopy.forEach(this::propagateHelper);
            throw new InconsistencyException();
        }
        return domainsCopy;
    }

    private <E> void propagateHelper(Variable<E> variable, Set<E> set) {
        variable.setCurrentDomain(set);
    }

    @Override
    public boolean satisfied() {
        return !lvariable.isAssigned()
                || !premise.test(lvariable.getValue())
                || (rvariable.isAssigned() && !filter.test(rvariable.getValue()))
                || rvariable.getCurrentDomain().stream().allMatch(filter.negate());
    }

    @Override
    public Set<Variable> getVariables() {
        return this.variables;
    }
}
